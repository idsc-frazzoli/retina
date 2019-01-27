// code by ynager
package ch.ethz.idsc.demo.mh;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Objects;
import java.util.function.Consumer;

import ch.ethz.idsc.gokart.core.map.BSplineTrack;
import ch.ethz.idsc.gokart.core.map.BayesianOccupancyGrid;
import ch.ethz.idsc.gokart.core.map.MappingConfig;
import ch.ethz.idsc.gokart.core.map.TrackReconConfig;
import ch.ethz.idsc.gokart.core.map.TrackReconManagement;
import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmServer;
import ch.ethz.idsc.gokart.core.pos.GokartPoseOdometry;
import ch.ethz.idsc.gokart.core.pos.MappedPoseInterface;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.top.GokartRender;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.lidar.VelodyneLcmChannels;
import ch.ethz.idsc.owl.car.core.VehicleModel;
import ch.ethz.idsc.owl.car.shop.RimoSinusIonModel;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.lidar.LidarAngularFiringCollector;
import ch.ethz.idsc.retina.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.lidar.LidarRotationProvider;
import ch.ethz.idsc.retina.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.lidar.VelodyneModel;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16SegmentProvider;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

// TODO contains redundancies with GokartMappingModule 
public class MappingAnalysisOfflineHighResMH implements OfflineLogListener, LidarRayBlockListener {
  private static final VehicleModel VEHICLE_MODEL = RimoSinusIonModel.standard();
  private static final String CHANNEL_LIDAR = //
      VelodyneLcmChannels.ray(VelodyneModel.VLP16, GokartLcmChannel.VLP16_CENTER);
  // ---
  private final VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
  private final BayesianOccupancyGrid bayesianOccupancyGrid;
  private final BayesianOccupancyGrid bayesianOccupancyGridThin;
  private final TrackReconManagement trackReconManagement;
  private final Consumer<BufferedImage> consumer;
  // ---
  private GokartPoseEvent gokartPoseEvent;
  private GokartPoseOdometry gokartPoseOdometry = GokartPoseLcmServer.INSTANCE.getGokartPoseOdometry();
  private MappedPoseInterface gokartPoseInterface = gokartPoseOdometry;
  private Scalar time_next = Quantity.of(0, SI.SECOND);
  private Scalar delta = Quantity.of(0.1, SI.SECOND);
  private SpacialXZObstaclePredicate predicate = TrackReconConfig.GLOBAL.createSpacialXZObstaclePredicate();

  public MappingAnalysisOfflineHighResMH(MappingConfig mappingConfig, Consumer<BufferedImage> consumer) {
    this.consumer = consumer;
    bayesianOccupancyGrid = mappingConfig.createTrackFittingBayesianOccupancyGrid();
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
    trackReconManagement = new TrackReconManagement(bayesianOccupancyGrid);
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
      gokartPoseEvent = new GokartPoseEvent(byteBuffer);
      bayesianOccupancyGrid.setPose(gokartPoseEvent.getPose());
      bayesianOccupancyGridThin.setPose(gokartPoseEvent.getPose());
      if (!trackReconManagement.isStartSet())
        trackReconManagement.setStart(gokartPoseEvent);
      if (count++ > 5)
        trackReconManagement.update(gokartPoseEvent, Quantity.of(0.05, SI.SECOND));
    } else if (channel.equals(CHANNEL_LIDAR)) {
      velodyneDecoder.lasers(byteBuffer);
    }
    // initialGuess.getControlPointGuess(RealScalar.of(40), RealScalar.of(0.5));
    if (Scalars.lessThan(time_next, time) && Objects.nonNull(gokartPoseEvent)) {
      time_next = time.add(delta);
      BufferedImage image = new BufferedImage(2000, 2000, BufferedImage.TYPE_3BYTE_BGR);
      double s = 50;
      int ox = -1200;
      int oy = 3300;
      final Tensor model2pixel = Tensors.matrix(new Number[][] { //
          { s, 0, ox }, //
          { 0, -s, oy }, //
          { 0, 0, 1 }, //
      }).unmodifiable();
      GeometricLayer gl = new GeometricLayer(model2pixel, Tensors.vector(0, 0, 0));
      Graphics2D graphics = image.createGraphics();
      gokartPoseInterface.setPose(gokartPoseEvent.getPose(), gokartPoseEvent.getQuality());
      GokartRender gr = new GokartRender(gokartPoseInterface, VEHICLE_MODEL);
      bayesianOccupancyGrid.render(gl, graphics);
      // bayesianOccupancyGridThin.render(gl, graphics);
      gr.render(gl, graphics);
      trackReconManagement.renderHR(gl, graphics);
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