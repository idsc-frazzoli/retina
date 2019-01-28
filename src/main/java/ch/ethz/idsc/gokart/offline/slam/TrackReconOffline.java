// code by ynager
// adapted by mh
package ch.ethz.idsc.gokart.offline.slam;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Objects;
import java.util.function.Consumer;

import ch.ethz.idsc.gokart.core.map.BayesianOccupancyGrid;
import ch.ethz.idsc.gokart.core.map.MappingConfig;
import ch.ethz.idsc.gokart.core.map.TrackReconConfig;
import ch.ethz.idsc.gokart.core.map.TrackReconManagement;
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
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

// TODO contains redundancies with GokartMappingModule 
public class TrackReconOffline implements OfflineLogListener, LidarRayBlockListener {
  private static final VehicleModel VEHICLE_MODEL = RimoSinusIonModel.standard();
  private static final String CHANNEL_LIDAR = //
      VelodyneLcmChannels.ray(VelodyneModel.VLP16, GokartLcmChannel.VLP16_CENTER);
  private static final Scalar DELTA = Quantity.of(0.1, SI.SECOND);
  // ---
  private final VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
  private final GokartPoseOdometry gokartPoseOdometry = GokartPoseLcmServer.INSTANCE.getGokartPoseOdometry();
  private final MappedPoseInterface gokartPoseInterface = gokartPoseOdometry;
  private final GokartRender gokartRender = new GokartRender(gokartPoseInterface, VEHICLE_MODEL);
  private final SpacialXZObstaclePredicate predicate = TrackReconConfig.GLOBAL.createSpacialXZObstaclePredicate();
  private final Consumer<BufferedImage> consumer;
  private final BayesianOccupancyGrid bayesianOccupancyGridThic;
  private final BayesianOccupancyGrid bayesianOccupancyGridThin;
  private final TrackReconManagement trackReconManagement;
  // ---
  private GokartPoseEvent gokartPoseEvent;
  private Scalar time_next = Quantity.of(0, SI.SECOND);

  public TrackReconOffline(MappingConfig mappingConfig, Consumer<BufferedImage> consumer) {
    this.consumer = consumer;
    bayesianOccupancyGridThic = mappingConfig.createTrackFittingBayesianOccupancyGrid();
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
    trackReconManagement = new TrackReconManagement(bayesianOccupancyGridThic);
  }

  private int count = 0;

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(GokartLcmChannel.POSE_LIDAR)) {
      gokartPoseEvent = new GokartPoseEvent(byteBuffer);
      bayesianOccupancyGridThic.setPose(gokartPoseEvent.getPose());
      bayesianOccupancyGridThin.setPose(gokartPoseEvent.getPose());
      if (!trackReconManagement.isStartSet())
        trackReconManagement.setStart(gokartPoseEvent);
      if (count++ > 5)
        trackReconManagement.update(gokartPoseEvent, Quantity.of(0.05, SI.SECOND));
    } else //
    if (channel.equals(CHANNEL_LIDAR))
      velodyneDecoder.lasers(byteBuffer);
    // ---
    if (Scalars.lessThan(time_next, time) && Objects.nonNull(gokartPoseEvent)) {
      time_next = time.add(DELTA);
      PredefinedMap predefinedMap = LocalizationConfig.getPredefinedMap();
      ScatterImage scatterImage = new WallScatterImage(predefinedMap);
      BufferedImage bufferedImage = scatterImage.getImage();
      GeometricLayer geometricLayer = GeometricLayer.of(predefinedMap.getModel2Pixel());
      Graphics2D graphics = bufferedImage.createGraphics();
      gokartPoseInterface.setPose(gokartPoseEvent.getPose(), gokartPoseEvent.getQuality());
      // bayesianOccupancyGridThic.render(gl, graphics);
      bayesianOccupancyGridThin.render(geometricLayer, graphics);
      gokartRender.render(geometricLayer, graphics);
      trackReconManagement.render(geometricLayer, graphics);
      // ---
      bayesianOccupancyGridThin.genObstacleMap();
      bayesianOccupancyGridThic.genObstacleMap();
      consumer.accept(bufferedImage);
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
        bayesianOccupancyGridThic.processObservation( //
            Tensors.vectorDouble(x, y), //
            isObstacle ? 1 : 0);
        bayesianOccupancyGridThin.processObservation( //
            Tensors.vectorDouble(x, y), //
            isObstacle ? 1 : 0);
      }
  }
}