// code by ynager, mh, jph
package ch.ethz.idsc.gokart.offline.slam;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import ch.ethz.idsc.gokart.core.map.BayesianOccupancyGrid;
import ch.ethz.idsc.gokart.core.map.MPCBSplineTrackRender;
import ch.ethz.idsc.gokart.core.map.MappingConfig;
import ch.ethz.idsc.gokart.core.map.TrackLayoutInitialGuess;
import ch.ethz.idsc.gokart.core.map.TrackReconConfig;
import ch.ethz.idsc.gokart.core.map.TrackReconManagement;
import ch.ethz.idsc.gokart.core.mpc.MPCBSplineTrack;
import ch.ethz.idsc.gokart.core.mpc.MPCBSplineTrackListener;
import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.slam.LocalizationConfig;
import ch.ethz.idsc.gokart.core.slam.PredefinedMap;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.top.GlobalGokartRender;
import ch.ethz.idsc.gokart.gui.top.GokartRender;
import ch.ethz.idsc.owl.gui.region.ImageRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.qty.Quantity;

public class TrackReconOffline extends LidarProcessOffline implements MPCBSplineTrackListener {
  private static final Scalar DELTA = Quantity.of(0.05, SI.SECOND);
  // ---
  private final SpacialXZObstaclePredicate spacialXZObstaclePredicate = //
      TrackReconConfig.GLOBAL.createSpacialXZObstaclePredicate();
  private final Consumer<BufferedImage> consumer;
  private final BayesianOccupancyGrid bayesianOccupancyGridThic;
  private final BayesianOccupancyGrid bayesianOccupancyGridThin;
  private final TrackReconManagement trackReconManagement;
  private final MPCBSplineTrackRender mpcBSplineTrackRender = new MPCBSplineTrackRender();
  private final TrackLayoutInitialGuess trackLayoutInitialGuess;
  // ---
  private GokartPoseEvent gokartPoseEvent;
  private Scalar time_next = Quantity.of(0, SI.SECOND);

  public TrackReconOffline(MappingConfig mappingConfig, Consumer<BufferedImage> consumer) {
    super(-4);
    this.consumer = consumer;
    bayesianOccupancyGridThic = mappingConfig.createTrackFittingBayesianOccupancyGrid();
    bayesianOccupancyGridThin = mappingConfig.createThinBayesianOccupancyGrid();
    trackReconManagement = new TrackReconManagement(bayesianOccupancyGridThic);
    // trackReconManagement = new TrackReconManagement(bayesianOccupancyGridThin);
    trackLayoutInitialGuess = trackReconManagement.getTrackLayoutInitialGuess();
  }

  private int count = 0;

  @Override
  protected void protected_event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(GokartLcmChannel.POSE_LIDAR)) {
      gokartPoseEvent = GokartPoseEvent.of(byteBuffer);
      bayesianOccupancyGridThic.setPose(gokartPoseEvent.getPose());
      bayesianOccupancyGridThin.setPose(gokartPoseEvent.getPose());
      if (!trackReconManagement.isStartSet())
        trackReconManagement.setStart(gokartPoseEvent.getPose());
    }
    // ---
    if (Scalars.lessThan(time_next, time) && Objects.nonNull(gokartPoseEvent)) {
      time_next = time.add(DELTA);
      if (count++ > 1) {
        // TODO JPH more elegant
        Optional<MPCBSplineTrack> optional = trackReconManagement.update(gokartPoseEvent, DELTA);
        mpcBSplineTrackRender.mpcBSplineTrack(optional);
      }
      PredefinedMap predefinedMap = LocalizationConfig.GLOBAL.getPredefinedMap();
      BufferedImage bufferedImage = new BufferedImage(640, 640, BufferedImage.TYPE_INT_ARGB);
      double zoom = 3;
      GeometricLayer geometricLayer = GeometricLayer.of(Tensors.matrix(new Number[][] { //
          { 7.5 * zoom, 0., -540 }, //
          { 0., -7.5 * zoom, 540 + 640 }, //
          { 0., 0., 1. } }));
      Tensor lastTrack = trackReconManagement.getTrackData();
      if (Objects.nonNull(lastTrack) && false)
        try {
          // TODO JPH
          File DIRECTORY = HomeDirectory.Pictures("mappercsv");
          File file = new File(DIRECTORY, "fielddata" + count + ".csv");
          Export.of(file, lastTrack.divide(Quantity.of(1, SI.METER)));
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      Graphics2D graphics = bufferedImage.createGraphics();
      ImageRender imageRender = ImageRender.of(predefinedMap.getImage(), predefinedMap.range());
      imageRender.render(geometricLayer, graphics);
      // bayesianOccupancyGridThic.render(geometricLayer, graphics);
      bayesianOccupancyGridThin.render(geometricLayer, graphics);
      GokartRender gokartRender = new GlobalGokartRender();
      gokartRender.gokartPoseListener.getEvent(gokartPoseEvent);
      gokartRender.render(geometricLayer, graphics);
      mpcBSplineTrackRender.render(geometricLayer, graphics);
      // trackLayoutInitialGuess.render(geometricLayer, graphics);
      // ---
      bayesianOccupancyGridThin.genObstacleMap();
      bayesianOccupancyGridThic.genObstacleMap();
      consumer.accept(bufferedImage);
    }
  }

  @Override
  protected void process(float x, float y, float z) {
    boolean isObstacle = spacialXZObstaclePredicate.isObstacle(x, z);
    // ---
    Tensor vector = Tensors.vectorDouble(x, y);
    int type = isObstacle ? 1 : 0;
    bayesianOccupancyGridThic.processObservation(vector, type);
    bayesianOccupancyGridThin.processObservation(vector, type);
  }

  @Override
  public void mpcBSplineTrack(Optional<MPCBSplineTrack> optional) {
    // ---
  }
}