// code by jph
package ch.ethz.idsc.demo.jph.video;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Optional;

import javax.imageio.ImageIO;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.FirstLogMessage;
import ch.ethz.idsc.gokart.offline.api.GokartLogAdapter;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.channel.GokartPoseChannel;
import ch.ethz.idsc.gokart.offline.slam.ObstacleAggregate;
import ch.ethz.idsc.gokart.offline.video.VideoBackground;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;

/** produces a high resolution image with lidar obstacles */
public enum RunVideoBackground {
  ;
  public static final Dimension DIMENSION = new Dimension(1920, 1080);
  // public static final Tensor MODEL2PIXEL = Tensors.fromString("{{50,0,-1000},{0,-50,3000},{0,0,1}}");
  // public static final Tensor MODEL2PIXEL = Tensors.fromString(
  // "{{21.57529078604976, 20.84482735590282, -1091.4861896725226}, {20.84482735590282, -21.57529078604976, 364.92043391882794}, {0.0, 0.0, 1.0}}");
  /** large */
  public static final Tensor _20190311 = Se2Utils.toSE2Translation(Tensors.vector(0, +200)).dot(DiagonalMatrix.of(0.9, 0.9, 1).dot(Tensors.fromString( //
      "{{36.67799433628459, 35.43620650503479, -1900.5265224432885}, {35.43620650503479, -36.67799433628459, 620.3647376620074}, {0.0, 0.0, 1.0}}")));
  /** dustproof wall */
  public static final Tensor _20190401 = Se2Utils.toSE2Translation(Tensors.vector(0, +120)).dot(Tensors.fromString( //
      "{{36.67799433628459, 35.43620650503479, -1900.5265224432885}, {35.43620650503479, -36.67799433628459, 620.3647376620074}, {0.0, 0.0, 1.0}}"));
  // ---
  private static final File IMAGE_FILE = HomeDirectory.Pictures("20190414T000000_00.png");

  public static VideoBackground get20190414() throws IOException {
    return VideoBackground.from(IMAGE_FILE, _20190401);
  }

  public static void main(String[] args) throws IOException {
    GokartLogInterface gokartLogInterface = //
        GokartLogAdapter.of(new File("/home/datahaki/ensemblelaps/pursuit/slow"), "m00.lcm");
    Optional<ByteBuffer> optional = FirstLogMessage.of(gokartLogInterface.file(), GokartPoseChannel.INSTANCE.channel());
    BufferedImage bufferedImage = new BufferedImage(DIMENSION.width, DIMENSION.height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = bufferedImage.createGraphics();
    graphics.setColor(Color.WHITE);
    graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
    graphics.setColor(new Color(0, 0, 0, 16));
    ObstacleAggregate obstacleAggregate = new ObstacleAggregate( //
        GokartPoseChannel.INSTANCE.channel(), //
        graphics, //
        _20190401, //
        GokartPoseEvent.of(optional.get()).getPose());
    OfflineLogPlayer.process(gokartLogInterface.file(), obstacleAggregate);
    ImageIO.write(bufferedImage, "png", IMAGE_FILE);
  }
}
