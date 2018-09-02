// code by ynager
package ch.ethz.idsc.gokart.offline.slam;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Objects;

import javax.imageio.ImageIO;

import ch.ethz.idsc.gokart.core.fuse.SafetyConfig;
import ch.ethz.idsc.gokart.core.map.BayesianOccupancyGrid;
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
import ch.ethz.idsc.retina.dev.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.dev.lidar.vlp16.Vlp16SegmentProvider;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;
import ch.ethz.idsc.retina.lcm.lidar.VelodyneLcmChannels;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Round;

public class MappingAnalysisOffline implements OfflineLogListener, LidarRayBlockListener {
  private static final VehicleModel VEHICLE_MODEL = RimoSinusIonModel.standard();
  private static final String CHANNEL_LIDAR = //
      VelodyneLcmChannels.ray(VelodyneModel.VLP16, GokartLcmChannel.VLP16_CENTER);
  // ---
  private final VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
  private final BayesianOccupancyGrid grid;
  private final Tensor gridRange = Tensors.vector(40, 40);
  private final Tensor lbounds = Tensors.vector(30, 30);
  private final File folder = UserHome.Pictures("log/mapper");
  // ---
  private GokartPoseEvent gpe;
  private ScatterImage scatterImage;
  private GokartPoseOdometry gokartPoseOdometry = GokartPoseLcmServer.INSTANCE.getGokartPoseOdometry();
  private MappedPoseInterface gokartPoseInterface = gokartPoseOdometry;
  private Scalar time_next = Quantity.of(0, SI.SECOND);
  private Scalar delta = Quantity.of(0.1, SI.SECOND);
  private SpacialXZObstaclePredicate predicate = SafetyConfig.GLOBAL.createSpacialXZObstaclePredicate();
  private int image_count = 0;

  public MappingAnalysisOffline() {
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
    grid = BayesianOccupancyGrid.of(lbounds, gridRange, //
        Quantity.of(0.2, SI.METER), //
        Quantity.of(0.4, SI.METER));
    folder.mkdirs();
    if (!folder.isDirectory())
      throw new RuntimeException();
  }

  @Override // from OfflineLogListener
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
      grid.genObstacleMap();
      try {
        ImageIO.write(image, "png", new File(folder, String.format("%06d.png", image_count++)));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Override // from LidarRayBlockListener
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    FloatBuffer floatBuffer = lidarRayBlockEvent.floatBuffer;
    if (Objects.nonNull(grid))
      if (lidarRayBlockEvent.dimensions == 3)
        while (floatBuffer.hasRemaining()) {
          float x = floatBuffer.get();
          float y = floatBuffer.get();
          float z = floatBuffer.get();
          //
          boolean isObstacle = predicate.isObstacle(x, z);
          Tensor planarPoint = Tensors.vectorDouble(x, y, 1);
          int type = isObstacle ? 1 : 0;
          grid.processObservation(planarPoint, type);
        }
  }
}