// code by jph
package ch.ethz.idsc.gokart.offline.slam;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import ch.ethz.idsc.gokart.core.fuse.SafetyConfig;
import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.lcm.lidar.VelodyneLcmChannels;
import ch.ethz.idsc.gokart.offline.api.GokartLogAdapter;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.pose.GokartPosePostChannel;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.lidar.*;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/** produces a high resolution image with lidar obstacles */
public class ObstacleAggregate implements OfflineLogListener, LidarSpacialListener {
  // public static final Tensor MODEL2PIXEL = Tensors.fromString("{{50,0,-1000},{0,-50,3000},{0,0,1}}");
  public static final Tensor MODEL2PIXEL = Tensors.fromString("{{43.158339, 41.68162, -2423}, {41.681623, -43.15833, 685}, {0.0, 0.0, 1.0}}").unmodifiable();
  private static final SpacialXZObstaclePredicate SPACIAL_XZ = //
      SafetyConfig.GLOBAL.createSpacialXZObstaclePredicate();
  private static final String CHANNEL_LIDAR = //
      VelodyneLcmChannels.ray(VelodyneModel.VLP16, GokartLcmChannel.VLP16_CENTER);
  // ---
  private final VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
  private final LidarSpacialProvider lidarSpacialProvider = SensorsConfig.GLOBAL.vlp16SpacialProvider();
  private final BufferedImage bufferedImage;
  private final Graphics2D graphics;
  private GeometricLayer geometricLayer = new GeometricLayer(MODEL2PIXEL, Array.zeros(3));
  private Tensor pose;

  public ObstacleAggregate(Tensor pose) {
    this.pose = pose;
    bufferedImage = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_ARGB);
    graphics = bufferedImage.createGraphics();
    graphics.setColor(Color.BLACK);
    graphics.setColor(Color.WHITE);
    graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
    lidarSpacialProvider.addListener(this);
    velodyneDecoder.addRayListener(lidarSpacialProvider);
    // graphics.setColor(new Color(255, 255, 255, 16));
    graphics.setColor(new Color(0, 0, 0, 16));
  }

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(CHANNEL_LIDAR))
      velodyneDecoder.lasers(byteBuffer);
    else //
    if (channel.equals(GokartPosePostChannel.INSTANCE.channel()
    // GokartPoseChannel.INSTANCE.channel() //
    )) {
      pose = new GokartPoseEvent(byteBuffer).getPose();
      geometricLayer = new GeometricLayer(MODEL2PIXEL, Array.zeros(3));
      geometricLayer.pushMatrix(GokartPoseHelper.toSE2Matrix(pose));
      geometricLayer.pushMatrix(Se2Utils.toSE2Translation(SensorsConfig.GLOBAL.vlp16));
    }
  }

  @Override // from LidarSpacialListener
  public void lidarSpacial(LidarXYZEvent lidarSpacialEvent) {
    float[] coords = lidarSpacialEvent.coords;
    if (SPACIAL_XZ.isObstacle(coords[0], coords[2])) { // x z
      Point2D point2d = geometricLayer.toPoint2D(coords[0], coords[1]); // x y
      graphics.fillRect((int) point2d.getX(), (int) point2d.getY(), 1, 1);
    }
  }

  public static void main(String[] args) throws IOException {
    File folder = new File("/media/datahaki/data/gokart/cuts/20190318/20190318T142605_05");
    GokartLogInterface gokartLogInterface = GokartLogAdapter.of(folder, "post.lcm");
    ObstacleAggregate obstacleAggregate = new ObstacleAggregate(gokartLogInterface.pose());
    OfflineLogPlayer.process(gokartLogInterface.file(), obstacleAggregate);
    ImageIO.write(obstacleAggregate.bufferedImage, "png", HomeDirectory.file(folder.getName() + ".png"));
  }
}
