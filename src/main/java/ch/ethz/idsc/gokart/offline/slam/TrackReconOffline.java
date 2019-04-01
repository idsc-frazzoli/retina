// code by ynager
// adapted by mh
package ch.ethz.idsc.gokart.offline.slam;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import ch.ethz.idsc.gokart.core.map.BayesianOccupancyGrid;
import ch.ethz.idsc.gokart.core.map.MappingConfig;
import ch.ethz.idsc.gokart.core.map.TrackLayoutInitialGuess;
import ch.ethz.idsc.gokart.core.map.TrackReconConfig;
import ch.ethz.idsc.gokart.core.map.TrackReconManagement;
import ch.ethz.idsc.gokart.core.map.TrackReconRender;
import ch.ethz.idsc.gokart.core.mpc.MPCBSplineTrack;
import ch.ethz.idsc.gokart.core.mpc.MPCBSplineTrackListener;
import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.core.pos.GokartPoseContainer;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.core.pos.MappedPoseInterface;
import ch.ethz.idsc.gokart.core.slam.PredefinedMap;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.top.GokartRender;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.lidar.VelodyneLcmChannels;
import ch.ethz.idsc.owl.gui.region.ImageRender;
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
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.qty.Quantity;

// TODO contains redundancies with GokartMappingModule 
public class TrackReconOffline implements OfflineLogListener, LidarRayBlockListener, MPCBSplineTrackListener {
  private static final File DIRECTORY = HomeDirectory.Pictures("log", "mapper");
  private static final String CHANNEL_LIDAR = //
      VelodyneLcmChannels.ray(VelodyneModel.VLP16, GokartLcmChannel.VLP16_CENTER);
  private static final Scalar DELTA = Quantity.of(0.05, SI.SECOND);
  // ---
  private final VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
  private final MappedPoseInterface gokartPoseInterface = new GokartPoseContainer();
  private final GokartRender gokartRender = new GokartRender();
  private final SpacialXZObstaclePredicate predicate = TrackReconConfig.GLOBAL.createSpacialXZObstaclePredicate();
  private final Consumer<BufferedImage> consumer;
  private final BayesianOccupancyGrid bayesianOccupancyGridThic;
  private final BayesianOccupancyGrid bayesianOccupancyGridThin;
  private final TrackReconManagement trackReconManagement;
  private final TrackReconRender trackReconRender = new TrackReconRender();
  private final TrackLayoutInitialGuess trackLayoutInitialGuess;
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
    LidarSpacialProvider lidarSpacialProvider = new Vlp16SegmentProvider(offset, -4);
    lidarSpacialProvider.addListener(lidarAngularFiringCollector);
    LidarRotationProvider lidarRotationProvider = new LidarRotationProvider();
    lidarRotationProvider.addListener(lidarAngularFiringCollector);
    velodyneDecoder.addRayListener(lidarSpacialProvider);
    velodyneDecoder.addRayListener(lidarRotationProvider);
    lidarAngularFiringCollector.addListener(this);
    trackReconManagement = new TrackReconManagement(bayesianOccupancyGridThic);
    // trackReconManagement = new TrackReconManagement(bayesianOccupancyGridThin);
    trackLayoutInitialGuess = trackReconManagement.getTrackLayoutInitialGuess();
  }

  private int count = 0;

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(GokartLcmChannel.POSE_LIDAR)) {
      gokartPoseEvent = GokartPoseEvent.of(byteBuffer);
      gokartRender.gokartPoseListener.getEvent(gokartPoseEvent);
      bayesianOccupancyGridThic.setPose(gokartPoseEvent.getPose());
      bayesianOccupancyGridThin.setPose(gokartPoseEvent.getPose());
      if (!trackReconManagement.isStartSet())
        trackReconManagement.setStart(gokartPoseEvent);
    } else //
    if (channel.equals(CHANNEL_LIDAR))
      velodyneDecoder.lasers(byteBuffer);
    // ---
    if (Scalars.lessThan(time_next, time) && Objects.nonNull(gokartPoseEvent)) {
      time_next = time.add(DELTA);
      if (count++ > 1) {
        Optional<MPCBSplineTrack> lastTrack = trackReconManagement.update(gokartPoseEvent, Quantity.of(0.05, SI.SECOND));
        trackReconRender.mpcBSplineTrack(lastTrack);
      }
      PredefinedMap predefinedMap = LocalizationConfig.getPredefinedMap();
      BufferedImage bufferedImage = new BufferedImage(640, 640, BufferedImage.TYPE_INT_ARGB);
      double zoom = 3;
      GeometricLayer geometricLayer = GeometricLayer.of(Tensors.matrix(new Number[][] { //
          { 7.5 * zoom, 0., -640 }, //
          { 0., -7.5 * zoom, 640 + 640 }, //
          { 0., 0., 1. }, //
      }));
      final File file = new File(DIRECTORY, "fielddata" + count + ".csv");
      Tensor lastTrack = trackReconManagement.getTrackData();
      if (Objects.nonNull(lastTrack))
        try {
          Export.of(file, lastTrack.divide(Quantity.of(1, SI.METER)));
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      Graphics2D graphics = bufferedImage.createGraphics();
      ImageRender imageRender = ImageRender.of(predefinedMap.getImage(), predefinedMap.range());
      imageRender.render(geometricLayer, graphics);
      gokartPoseInterface.setPose(gokartPoseEvent.getPose(), gokartPoseEvent.getQuality());
      // bayesianOccupancyGridThic.render(geometricLayer, graphics);
      bayesianOccupancyGridThin.render(geometricLayer, graphics);
      gokartRender.render(geometricLayer, graphics);
      trackReconRender.render(geometricLayer, graphics);
      trackLayoutInitialGuess.render(geometricLayer, graphics);
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

  @Override
  public void mpcBSplineTrack(Optional<MPCBSplineTrack> optional) {
    // TODO
    // Optional<MPCBSplineTrack> optional;
  }
}