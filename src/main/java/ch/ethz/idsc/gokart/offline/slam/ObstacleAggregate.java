// code by jph
package ch.ethz.idsc.gokart.offline.slam;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.core.fuse.SafetyConfig;
import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.lidar.VelodyneLcmChannels;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.lidar.LidarSpacialListener;
import ch.ethz.idsc.retina.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.lidar.LidarXYZEvent;
import ch.ethz.idsc.retina.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.lidar.VelodyneModel;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;

/** produces a high resolution image with lidar obstacles */
public class ObstacleAggregate implements OfflineLogListener, LidarSpacialListener {
  private static final SpacialXZObstaclePredicate SPACIAL_XZ = //
      SafetyConfig.GLOBAL.createSpacialXZObstaclePredicate();
  private static final String CHANNEL_LIDAR = //
      VelodyneLcmChannels.ray(VelodyneModel.VLP16, GokartLcmChannel.VLP16_CENTER);
  // ---
  private final VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
  private final LidarSpacialProvider lidarSpacialProvider = SensorsConfig.GLOBAL.vlp16SpacialProvider();
  private final Graphics2D graphics;
  private final String poseChannel;
  private final Tensor model2pixel;
  private GeometricLayer geometricLayer;

  /** @param poseChannel
   * @param graphics
   * @param model2pixel
   * @param pose */
  public ObstacleAggregate(String poseChannel, Graphics2D graphics, Tensor model2pixel, Tensor pose) {
    this.poseChannel = poseChannel;
    this.graphics = graphics;
    this.model2pixel = model2pixel;
    setPose(pose);
    lidarSpacialProvider.addListener(this);
    velodyneDecoder.addRayListener(lidarSpacialProvider);
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(CHANNEL_LIDAR))
      velodyneDecoder.lasers(byteBuffer);
    else //
    if (channel.equals(this.poseChannel))
      setPose(GokartPoseEvent.of(byteBuffer).getPose());
  }

  private void setPose(Tensor pose) {
    geometricLayer = new GeometricLayer(model2pixel, Array.zeros(3));
    geometricLayer.pushMatrix(PoseHelper.toSE2Matrix(pose));
    geometricLayer.pushMatrix(SensorsConfig.GLOBAL.vlp16Gokart());
  }

  @Override // from LidarSpacialListener
  public void lidarSpacial(LidarXYZEvent lidarXYZEvent) {
    float[] coords = lidarXYZEvent.coords;
    if (SPACIAL_XZ.isObstacle(coords[0], coords[2])) { // x z
      Point2D point2d = geometricLayer.toPoint2D(coords[0], coords[1]); // x y
      graphics.fillRect( //
          (int) point2d.getX(), //
          (int) point2d.getY(), 1, 1);
    }
  }
}
