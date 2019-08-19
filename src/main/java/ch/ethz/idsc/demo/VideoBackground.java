// code by jph
package ch.ethz.idsc.demo;

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
import ch.ethz.idsc.gokart.offline.map.ObstacleAggregate;
import ch.ethz.idsc.gokart.offline.video.BackgroundImage;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;

/** produces a high resolution image with lidar obstacles */
public enum VideoBackground {
  ;
  public static final Dimension DIMENSION = new Dimension(1920, 1080);
  // public static final Tensor MODEL2PIXEL = Tensors.fromString("{{50, 0, -1000}, {0, -50, 3000}, {0, 0, 1}}");
  // public static final Tensor MODEL2PIXEL = Tensors.fromString(
  // "{{21.57529078604976, 20.84482735590282, -1091.4861896725226}, {20.84482735590282, -21.57529078604976, 364.92043391882794}, {0.0, 0.0, 1.0}}");
  public static final Tensor _20190309 = Tensors.fromString( //
      "{{42.72771097503904, 42.122947603812364, -4322.645828532184}, {42.122947603812364, -42.72771097503904, 729.4840362558339}, {0.0, 0.0, 1.0}}");
  // "{{42.72771097503904, 42.122947603812364, -4498.645828532184}, {42.122947603812364, -42.72771097503904, 626.4840362558339}, {0.0, 0.0, 1.0}}");
  /** large */
  public static final Tensor _20190311 = Se2Matrix.translation(Tensors.vector(0, +200)).dot(DiagonalMatrix.of(0.9, 0.9, 1).dot(Tensors.fromString( //
      "{{36.67799433628459, 35.43620650503479, -1900.5265224432885}, {35.43620650503479, -36.67799433628459, 620.3647376620074}, {0.0, 0.0, 1.0}}")));
  /** dustproof wall */
  public static final Tensor _20190401 = Se2Matrix.translation(Tensors.vector(0, +120)).dot(Tensors.fromString( //
      "{{36.67799433628459, 35.43620650503479, -1900.5265224432885}, {35.43620650503479, -36.67799433628459, 620.3647376620074}, {0.0, 0.0, 1.0}}"));
  // ---

  public static BackgroundImage get20190414() throws IOException {
    return BackgroundImage.from(HomeDirectory.Pictures("20190408T000000_00.png"), _20190401);
  }

  public static BackgroundImage get20190530() throws IOException {
    return BackgroundImage.from(HomeDirectory.Pictures("20190530T143412_00.png"), _20190401);
  }

  public static BackgroundImage get20190606() throws IOException {
    return BackgroundImage.from(HomeDirectory.Pictures("20190606T160956_00.png"), _20190401);
  }

  public static BackgroundImage get20190610() throws IOException {
    return BackgroundImage.from(HomeDirectory.Pictures("20190610T154922_00.png"), _20190401);
  }

  public static BackgroundImage get20190701() throws IOException {
    return BackgroundImage.from(HomeDirectory.Pictures("20190701T175650_00.png"), _20190401);
  }

  public static BackgroundImage get20190729a() throws IOException {
    return BackgroundImage.from(HomeDirectory.Pictures("20190729T115559_00.png"), _20190401);
  }

  public static BackgroundImage get20190729b() throws IOException {
    return BackgroundImage.from(HomeDirectory.Pictures("20190729T140711_00.png"), _20190401);
  }

  public static BackgroundImage auto(File file) throws IOException {
    File directory = file.getParentFile();
    return BackgroundImage.from(new File(directory, directory.getName() + ".bck.png"), _20190401);
  }

  /** @param lcm_log
   * @param model2pixel
   * @param image
   * @throws IOException */
  public static void render(File lcm_log, Tensor model2pixel, File image) throws IOException {
    Optional<ByteBuffer> optional = FirstLogMessage.of(lcm_log, GokartPoseChannel.INSTANCE.channel());
    BufferedImage bufferedImage = new BufferedImage(DIMENSION.width, DIMENSION.height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = bufferedImage.createGraphics();
    graphics.setColor(Color.WHITE);
    graphics.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
    graphics.setColor(new Color(0, 0, 0, 16));
    ObstacleAggregate obstacleAggregate = new ObstacleAggregate( //
        GokartPoseChannel.INSTANCE.channel(), //
        graphics, //
        model2pixel, //
        GokartPoseEvent.of(optional.get()).getPose());
    OfflineLogPlayer.process(lcm_log, obstacleAggregate);
    ImageIO.write(bufferedImage, "png", image);
  }

  public static void render(File directory) throws IOException {
    GokartLogInterface gokartLogInterface = //
        GokartLogAdapter.of(directory);
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
    System.out.print("processing... ");
    OfflineLogPlayer.process(gokartLogInterface.file(), obstacleAggregate);
    System.out.print("finished");
    ImageIO.write(bufferedImage, "png", new File(directory, directory.getName() + ".bck.png"));
  }
}
