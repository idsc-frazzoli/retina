// code by jph
package ch.ethz.idsc.demo.jph.lidar;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import ch.ethz.idsc.gokart.core.fuse.SafetyConfig;
import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.core.slam.PredefinedMap;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.lcm.lidar.VelodyneLcmChannels;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.lidar.LidarSpacialEvent;
import ch.ethz.idsc.retina.lidar.LidarSpacialListener;
import ch.ethz.idsc.retina.lidar.LidarSpacialProvider;
import ch.ethz.idsc.retina.lidar.VelodyneDecoder;
import ch.ethz.idsc.retina.lidar.VelodyneModel;
import ch.ethz.idsc.retina.lidar.vlp16.Vlp16Decoder;
import ch.ethz.idsc.retina.util.img.ImageCopy;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;

/* package */ class ObstacleAggregation implements OfflineLogListener, LidarSpacialListener {
  private static final PredefinedMap PREDEFINED_MAP = LocalizationConfig.getPredefinedMap();
  private static final Tensor MODEL2PIXEL = PREDEFINED_MAP.getModel2Pixel();
  private static final Tensor LIDAR = SensorsConfig.GLOBAL.vlp16Gokart();
  private static final String CHANNEL_LIDAR = //
      VelodyneLcmChannels.ray(VelodyneModel.VLP16, GokartLcmChannel.VLP16_CENTER);
  private static final SpacialXZObstaclePredicate SPACIAL_XZ = //
      SafetyConfig.GLOBAL.createSpacialXZObstaclePredicate();
  // ---
  private final LidarSpacialProvider lidarSpacialProvider = SensorsConfig.GLOBAL.vlp16SpacialProvider();
  private final VelodyneDecoder velodyneDecoder = new Vlp16Decoder();
  private GeometricLayer geometricLayer = new GeometricLayer(IdentityMatrix.of(3), Array.zeros(3));
  private boolean initialized = false;
  private final BufferedImage bufferedImage;
  private final Graphics2D graphics;

  public ObstacleAggregation() {
    lidarSpacialProvider.addListener(this);
    velodyneDecoder.addRayListener(lidarSpacialProvider);
    ImageCopy imageCopy = new ImageCopy();
    imageCopy.update(PREDEFINED_MAP.getImage());
    bufferedImage = imageCopy.get();
    graphics = bufferedImage.createGraphics();
    graphics.setColor(Color.WHITE);
  }

  @Override // from OfflineLogListener
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    if (channel.equals(CHANNEL_LIDAR))
      velodyneDecoder.lasers(byteBuffer);
    else //
    if (channel.equals(GokartLcmChannel.POSE_LIDAR)) {
      GokartPoseInterface gokartPoseInterface = new GokartPoseEvent(byteBuffer);
      geometricLayer = new GeometricLayer(MODEL2PIXEL, Array.zeros(3));
      geometricLayer.pushMatrix(GokartPoseHelper.toSE2Matrix(gokartPoseInterface.getPose()));
      geometricLayer.pushMatrix(LIDAR);
      initialized = true;
    }
  }

  @Override // from LidarSpacialListener
  public void lidarSpacial(LidarSpacialEvent lidarSpacialEvent) {
    float[] coords = lidarSpacialEvent.coords;
    if (SPACIAL_XZ.isObstacle(coords[0], coords[2]) && initialized) { // x z
      Point2D point2d = geometricLayer.toPoint2D(coords[0], coords[1]); // x y
      graphics.fillRect((int) point2d.getX(), (int) point2d.getY(), 1, 1);
    }
  }

  public static void main(String[] args) throws IOException {
    ObstacleAggregation obstacleAggergation = new ObstacleAggregation();
    OfflineLogPlayer.process(HomeDirectory.file("gokart/pedestrian/20180604T150508/log.lcm"), obstacleAggergation);
    ImageIO.write(obstacleAggergation.bufferedImage, "png", HomeDirectory.Pictures("obstacles.png"));
  }
}
