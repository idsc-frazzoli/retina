// code by ynager
package ch.ethz.idsc.gokart.offline.slam;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Objects;
import java.util.function.Consumer;

import ch.ethz.idsc.gokart.core.fuse.SafetyConfig;
import ch.ethz.idsc.gokart.core.map.BayesianOccupancyGrid;
import ch.ethz.idsc.gokart.core.map.MappingConfig;
import ch.ethz.idsc.gokart.core.mpc.BSplineTrack;
import ch.ethz.idsc.gokart.core.mpc.MPCBSplineTrack;
import ch.ethz.idsc.gokart.core.mpc.TrackLayoutInitialGuess;
import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmServer;
import ch.ethz.idsc.gokart.core.pos.GokartPoseOdometry;
import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.core.pos.MappedPoseInterface;
import ch.ethz.idsc.gokart.core.slam.PredefinedMap;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.top.GokartRender;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.gui.top.TrackRender;
import ch.ethz.idsc.owl.car.core.VehicleModel;
import ch.ethz.idsc.owl.car.shop.RimoSinusIonModel;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.dev.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.dev.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.dev.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.dev.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.dev.lidar.VelodyneModel;
import ch.ethz.idsc.retina.dev.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.dev.lidar.vlp16.Vlp16SegmentProvider;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.retina.lcm.lidar.VelodyneLcmChannels;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.LinearSolve;
import ch.ethz.idsc.tensor.qty.Quantity;

// TODO contains redundancies with GokartMappingModule 
public class MappingAnalysisOfflineMH implements OfflineLogListener, LidarRayBlockListener {
  private static final VehicleModel VEHICLE_MODEL = RimoSinusIonModel.standard();
  private static final String CHANNEL_LIDAR = //
      VelodyneLcmChannels.ray(VelodyneModel.VLP16, GokartLcmChannel.VLP16_CENTER);
  // ---
  private final VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
  private final BayesianOccupancyGrid bayesianOccupancyGrid;
  private final BayesianOccupancyGrid bayesianOccupancyGridThin;
  private final TrackLayoutInitialGuess initialGuess;
  private final Consumer<BufferedImage> consumer;
  // ---
  private GokartPoseEvent gpe;
  private ScatterImage scatterImage;
  private GokartPoseOdometry gokartPoseOdometry = GokartPoseLcmServer.INSTANCE.getGokartPoseOdometry();
  private MappedPoseInterface gokartPoseInterface = gokartPoseOdometry;
  private Scalar time_next = Quantity.of(0, SI.SECOND);
  private Scalar delta = Quantity.of(0.1, SI.SECOND);
  private SpacialXZObstaclePredicate predicate = SafetyConfig.GLOBAL.createSpacialXZObstaclePredicate();

  public MappingAnalysisOfflineMH(MappingConfig mappingConfig, Consumer<BufferedImage> consumer) {
    this.consumer = consumer;
    bayesianOccupancyGrid = mappingConfig.createBayesianOccupancyGrid();
    bayesianOccupancyGridThin = mappingConfig.createThinBayesianOccupancyGrid();
    LidarAngularFiringCollector lidarAngularFiringCollector = //
        new LidarAngularFiringCollector(10000, 3);
    double offset = SensorsConfig.GLOBAL.vlp16_twist.number().doubleValue();
    LidarSpacialProvider lidarSpacialProvider = new Vlp16SegmentProvider(offset, -6);
    lidarSpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    velodyneDecoder.addRayListener(lidarSpacialProvider);
    velodyneDecoder.addRayListener(lidarRotationProvider);
    lidarAngularFiringCollector.addListener(this);
    initialGuess = new TrackLayoutInitialGuess(bayesianOccupancyGrid);
  }

  int count = 0;
  int startX = -1;
  int startY = -1;
  Tensor trackData = null;
  double startOrientation;
  BSplineTrack track = null;

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(GokartLcmChannel.POSE_LIDAR)) {
      gpe = new GokartPoseEvent(byteBuffer);
      bayesianOccupancyGrid.setPose(gpe.getPose());
      bayesianOccupancyGridThin.setPose(gpe.getPose());
      count++;
      if (startX == -1) {
        Tensor transform = bayesianOccupancyGrid.getTransform();
        System.out.println(transform);
        Tensor startPos = gpe.getPose();
        Tensor hPos = startPos;
        hPos.set(Quantity.of(1, SI.METER), 2);
        Tensor pixelPos = LinearSolve.of(transform, hPos);
        System.out.println(startPos);
        System.out.println(pixelPos);
        startX = pixelPos.Get(0).number().intValue();
        startY = pixelPos.Get(1).number().intValue();
        startOrientation = startPos.Get(2).number().doubleValue();
      }
    } else if (channel.equals(CHANNEL_LIDAR)) {
      velodyneDecoder.lasers(byteBuffer);
    }
    // initialGuess.getControlPointGuess(RealScalar.of(40), RealScalar.of(0.5));
    if (Scalars.lessThan(time_next, time) && Objects.nonNull(gpe)) {
      time_next = time.add(delta);
      PredefinedMap predefinedMap = LocalizationConfig.getPredefinedMap();
      scatterImage = new WallScatterImage(predefinedMap);
      BufferedImage image = scatterImage.getImage();
      GeometricLayer gl = new GeometricLayer(predefinedMap.getModel2Pixel(), Tensors.vector(0, 0, 0));
      Graphics2D graphics = image.createGraphics();
      gokartPoseInterface.setPose(gpe.getPose(), gpe.getQuality());
      GokartRender gr = new GokartRender(gokartPoseInterface, VEHICLE_MODEL);
      //bayesianOccupancyGrid.render(gl, graphics);
      bayesianOccupancyGridThin.render(gl, graphics);
      gr.render(gl, graphics);
      if (true) {
        if (trackData == null) {
          initialGuess.update(startX, startY, startOrientation, gpe.getPose(), track);
          initialGuess.render(gl, graphics);
          if (initialGuess.isClosed()) {
            Scalar spacing = RealScalar.of(1.5);
            Scalar controlPointResolution = RealScalar.of(0.5);
            Tensor ctrpoints = initialGuess.getControlPointGuess(spacing, controlPointResolution);
            if (ctrpoints != null) {
              Tensor radiusCtrPoints = Tensors.empty();
              for (int i = 0; i < ctrpoints.get(0).length(); i++) {
                radiusCtrPoints.append(Quantity.of(1, SI.METER));
              }
              trackData = initialGuess.getRefinedTrack(//
                  ctrpoints.get(0), //
                  ctrpoints.get(1), //
                  radiusCtrPoints, RealScalar.of(8), 100);
            } else {
              System.out.println("no sensible track found!");
            }
          }
        } else {
          System.out.println("refining old track!");
          trackData = initialGuess.getRefinedTrack(//
              trackData, RealScalar.of(8), 1);
        }
      }
      if (trackData != null) {
        Tensor radCtrP = Tensors.vector((i)-> trackData.get(2).Get(i).add(Quantity.of(0.7,SI.METER)),trackData.get(2).length());
        track = new BSplineTrack(trackData.get(0), trackData.get(1), radCtrP);
      }
      if(track != null) {
        TrackRender trackRender = new TrackRender(track);
        trackRender.render(gl, graphics);
      }
      // if (Scalars.lessEquals(RealScalar.of(3), Magnitude.SECOND.apply(time)) && flag == false) {
      // grid.setNewlBound(Tensors.vector(20, 20));
      // flag = true;
      // }
      // ---
      // grid.genObstacleMap();
      // System.out.println(time);
      bayesianOccupancyGridThin.genObstacleMap();
      bayesianOccupancyGrid.genObstacleMap();
      consumer.accept(image);
    }
  }

  @Override // from LidarRayBlockListener
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    FloatBuffer floatBuffer = lidarRayBlockEvent.floatBuffer;
    if (lidarRayBlockEvent.dimensions == 3)
      while (floatBuffer.hasRemaining()) {
        float x = floatBuffer.get();
        float y = floatBuffer.get();
        float z = floatBuffer.get();
        //
        boolean isObstacle = predicate.isObstacle(x, z);
        bayesianOccupancyGrid.processObservation( //
            Tensors.vectorDouble(x, y), //
            isObstacle ? 1 : 0);
        bayesianOccupancyGridThin.processObservation( //
            Tensors.vectorDouble(x, y), //
            isObstacle ? 1 : 0);
      }
  }
}