// code by ynager
package ch.ethz.idsc.demo.yn;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Objects;

import javax.imageio.ImageIO;

import ch.ethz.idsc.gokart.core.perc.SimpleSpacialObstaclePredicate;
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
import ch.ethz.idsc.gokart.offline.slam.ScatterImage;
import ch.ethz.idsc.gokart.offline.slam.WallScatterImage;
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
import ch.ethz.idsc.retina.dev.lidar.vlp16.Vlp16SpacialProvider;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.retina.lcm.lidar.VelodyneLcmChannels;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.subare.util.UserHome;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

class MappingAnalysis implements OfflineLogListener, LidarRayBlockListener {
  private static final VehicleModel VEHICLE_MODEL = RimoSinusIonModel.standard();
  // ---
  private GokartPoseEvent gpe;
  private ScatterImage scatterImage;
  private GokartPoseOdometry gokartPoseOdometry = GokartPoseLcmServer.INSTANCE.getGokartPoseOdometry();
  private MappedPoseInterface gokartPoseInterface = gokartPoseOdometry;
  private Scalar time_next = Quantity.of(0, SI.SECOND);
  private Scalar delta = Quantity.of(0.1, SI.SECOND);
  private final VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
  private SpacialXZObstaclePredicate predicate = new SimpleSpacialObstaclePredicate( //
      Quantity.of(-0.9, SI.METER), //
      Quantity.of(1.0, SI.METER), //
      SensorsConfig.GLOBAL.vlp16_incline //
  );
  private static final String CHANNEL_LIDAR = //
      VelodyneLcmChannels.ray(VelodyneModel.VLP16, GokartLcmChannel.VLP16_CENTER);
  private final BayesianOccupancyGrid grid;
  private int counter = 0;
  private final Tensor lidar2gokart = SensorsConfig.GLOBAL.vlp16Gokart();
  private final static Tensor GRID2IMAGE = Tensors.matrixDouble(new double[][] { { 7.5, 0, 0 }, { 0, 7.5, 0 }, { 0, 0, 1 } });
  // private final Tensor gridRange = Tensors.vector(85, 85, 1);
  private final Tensor gridRange = PredefinedMap.DUBENDORF_HANGAR_20180506.range();
  // private final Tensor imageRange = GRID2IMAGE.dot(gridRange);
  private final Tensor imageRange = gridRange;

  public MappingAnalysis() {
    LidarAngularFiringCollector lidarAngularFiringCollector = new LidarAngularFiringCollector(15000, 3);
    double offset = SensorsConfig.GLOBAL.vlp16_twist.number().doubleValue();
    LidarSpacialProvider lidarSpacialProvider = new Vlp16SpacialProvider(offset);
    lidarSpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    velodyneDecoder.addRayListener(lidarSpacialProvider);
    velodyneDecoder.addRayListener(lidarRotationProvider);
    lidarAngularFiringCollector.addListener(this);
    // ---
    Tensor lbounds = Tensors.vector(0, 0);
    grid = BayesianOccupancyGrid.of(lbounds, gridRange.extract(0, 2), DoubleScalar.of(0.2));
    grid.setObstacleRadius(DoubleScalar.of(0.8));
  }

  // ---
  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(GokartLcmChannel.POSE_LIDAR)) {
      gpe = new GokartPoseEvent(byteBuffer);
      grid.setPose(gpe.getPose());
    } else if (channel.equals(CHANNEL_LIDAR)) {
      velodyneDecoder.lasers(byteBuffer);
    }
    if (Scalars.lessThan(time_next, time) && Objects.nonNull(gpe)) {
      time_next = time.add(delta);
      // System.out.print("Extracting log at " + time.map(Round._2) + "\n");
      PredefinedMap predefinedMap = LocalizationConfig.getPredefinedMap();
      scatterImage = new WallScatterImage(predefinedMap);
      BufferedImage image = scatterImage.getImage();
      GeometricLayer gl = new GeometricLayer(predefinedMap.getModel2Pixel(), Tensors.vector(0, 0, 0));
      Graphics2D graphics = (Graphics2D) image.getGraphics();
      gokartPoseInterface.setPose(gpe.getPose(), gpe.getQuality());
      GokartRender gr = new GokartRender(gokartPoseInterface, VEHICLE_MODEL);
      grid.render(gl, graphics);
      gr.render(gl, graphics);
      if (Scalars.lessEquals(RealScalar.of(3), Magnitude.SECOND.apply(time)))
        grid.setGridCenter(Tensors.vector(50, 50));
      // ---
      try {
        grid.genObstacleMap();
        ImageIO.write(image, "png", UserHome.Pictures("/log/" + Magnitude.SECOND.apply(time).toString() + ".png"));
        System.out.println("writing img: " + counter++);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String[] args) throws FileNotFoundException, IOException {
    // File file = YnLogFileLocator.file(GokartLogFile._20180503T160522_16144bb6);
    File file = new File("/home/ynager/gokart/logs/20180503/20180503T160522_short.lcm");
    OfflineLogListener oll = new MappingAnalysis();
    OfflineLogPlayer.process(file, oll);
    System.out.print("Done.");
  }

  @Override
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    FloatBuffer floatBuffer = lidarRayBlockEvent.floatBuffer;
    if (Objects.nonNull(lidar2gokart))
      if (lidarRayBlockEvent.dimensions == 3)
        while (floatBuffer.hasRemaining()) {
          double x = floatBuffer.get();
          double y = floatBuffer.get();
          double z = floatBuffer.get();
          // // no filter based on height
          if (z <= 0.1) {
            boolean isObstacle = predicate.isObstacle(x, z);
            Tensor planarPoint = Tensors.vectorDouble(x, y, 1); // TODO insert lidar transform
            // Tensor gokartPoint = lidar2gokart.dot(planarPoint);
            // Tensor worldPoint = lidar2world.dot(planarPoint);
            int type = isObstacle ? 1 : 0;
            grid.processObservation(planarPoint, type);
          }
        }
  }
}