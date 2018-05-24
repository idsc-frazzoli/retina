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

import ch.ethz.idsc.gokart.core.map.BayesianOccupancyGrid;
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
import ch.ethz.idsc.owl.bot.util.UserHome;
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
import ch.ethz.idsc.retina.dev.lidar.app.Vlp16SegmentProvider;
import ch.ethz.idsc.retina.dev.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.retina.lcm.OfflineLogPlayer;
import ch.ethz.idsc.retina.lcm.lidar.VelodyneLcmChannels;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Round;

class MappingAnalysis implements OfflineLogListener, LidarRayBlockListener {
  private static final VehicleModel VEHICLE_MODEL = RimoSinusIonModel.standard();
  // ---
  private GokartPoseEvent gpe;
  private ScatterImage scatterImage;
  private GokartPoseOdometry gokartPoseOdometry = GokartPoseLcmServer.INSTANCE.getGokartPoseOdometry();
  private MappedPoseInterface gokartPoseInterface = gokartPoseOdometry;
  private Scalar time_next = Quantity.of(0, SI.SECOND);
  private Scalar delta = Quantity.of(0.2, SI.SECOND);
  private final VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
  private SpacialXZObstaclePredicate predicate = SimpleSpacialObstaclePredicate.createVlp16();
  private static final String CHANNEL_LIDAR = //
      VelodyneLcmChannels.ray(VelodyneModel.VLP16, GokartLcmChannel.VLP16_CENTER);
  private final BayesianOccupancyGrid grid;
  private final Tensor gridRange = Tensors.vector(85, 85);
  private final Tensor lbounds;
  private boolean flag = false;

  public MappingAnalysis() {
    LidarAngularFiringCollector lidarAngularFiringCollector = //
        new LidarAngularFiringCollector(10000, 3);
    double offset = SensorsConfig.GLOBAL.vlp16_twist.number().doubleValue();
    LidarSpacialProvider lidarSpacialProvider = new Vlp16SegmentProvider(offset, -1);
    lidarSpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    velodyneDecoder.addRayListener(lidarSpacialProvider);
    velodyneDecoder.addRayListener(lidarRotationProvider);
    lidarAngularFiringCollector.addListener(this);
    // ---
    lbounds = Tensors.vector(0, 0);
    grid = BayesianOccupancyGrid.of(lbounds, gridRange.extract(0, 2), DoubleScalar.of(0.2));
    grid.setObstacleRadius(DoubleScalar.of(0.4));
  }

  // ---
  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(GokartLcmChannel.POSE_LIDAR)) {
      gpe = new GokartPoseEvent(byteBuffer);
      grid.setPose(gpe.getPose(), gpe.getQuality());
    } else if (channel.equals(CHANNEL_LIDAR)) {
      velodyneDecoder.lasers(byteBuffer);
    }
    if (Scalars.lessThan(time_next, time) && Objects.nonNull(gpe)) {
      time_next = time.add(delta);
      System.out.print("Extracting log at " + time.map(Round._2) + "\n");
      PredefinedMap predefinedMap = LocalizationConfig.getPredefinedMap();
      scatterImage = new WallScatterImage(predefinedMap);
      BufferedImage image = scatterImage.getImage();
      GeometricLayer gl = new GeometricLayer(predefinedMap.getModel2Pixel(), Tensors.vector(0, 0, 0));
      Graphics2D graphics = image.createGraphics();
      gokartPoseInterface.setPose(gpe.getPose(), gpe.getQuality());
      GokartRender gr = new GokartRender(gokartPoseInterface, VEHICLE_MODEL);
      grid.render(gl, graphics);
      gr.render(gl, graphics);
      // if (Scalars.lessEquals(RealScalar.of(3), Magnitude.SECOND.apply(time)) && flag == false) {
      // grid.setNewlBound(Tensors.vector(20, 20));
      // flag = true;
      // }
      // ---
      // grid.genObstacleMap();
      // System.out.println(time);
      try {
        grid.genObstacleMap();
        ImageIO.write(image, "png", UserHome.Pictures("/log/" + Magnitude.SECOND.apply(time).toString() + ".png"));
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
    if (Objects.nonNull(grid))
      if (lidarRayBlockEvent.dimensions == 3)
        while (floatBuffer.hasRemaining()) {
          double x = floatBuffer.get();
          double y = floatBuffer.get();
          double z = floatBuffer.get();
          //
          boolean isObstacle = predicate.isObstacle(x, z);
          Tensor planarPoint = Tensors.vectorDouble(x, y, 1);
          int type = isObstacle ? 1 : 0;
          grid.processObservation(planarPoint, type);
        }
  }
}