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

import ch.ethz.idsc.gokart.core.map.DubendorfFrame;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.lcm.OfflineLogPlayer;
import ch.ethz.idsc.gokart.offline.api.FirstLogMessage;
import ch.ethz.idsc.gokart.offline.api.GokartLogAdapter;
import ch.ethz.idsc.gokart.offline.api.GokartLogInterface;
import ch.ethz.idsc.gokart.offline.channel.GokartPoseChannel;
import ch.ethz.idsc.gokart.offline.map.ObstacleAggregate;
import ch.ethz.idsc.gokart.offline.video.BackgroundImage;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/** produces a high resolution image with lidar obstacles */
public enum VideoBackground {
  ;
  public static final Dimension DIMENSION = new Dimension(1920, 1080);

  public static BackgroundImage get20190414() throws IOException {
    return BackgroundImage.from(HomeDirectory.Pictures("20190408T000000_00.png"), DubendorfFrame._20190401);
  }

  public static BackgroundImage get20190530() throws IOException {
    return BackgroundImage.from(HomeDirectory.Pictures("20190530T143412_00.png"), DubendorfFrame._20190401);
  }

  public static BackgroundImage get20190606() throws IOException {
    return BackgroundImage.from(HomeDirectory.Pictures("20190606T160956_00.png"), DubendorfFrame._20190401);
  }

  public static BackgroundImage get20190610() throws IOException {
    return BackgroundImage.from(HomeDirectory.Pictures("20190610T154922_00.png"), DubendorfFrame._20190401);
  }

  public static BackgroundImage get20190701() throws IOException {
    return BackgroundImage.from(HomeDirectory.Pictures("20190701T175650_00.png"), DubendorfFrame._20190401);
  }

  public static BackgroundImage get20190729a() throws IOException {
    return BackgroundImage.from(HomeDirectory.Pictures("20190729T115559_00.png"), DubendorfFrame._20190401);
  }

  public static BackgroundImage get20190729b() throws IOException {
    return BackgroundImage.from(HomeDirectory.Pictures("20190729T140711_00.png"), DubendorfFrame._20190401);
  }

  public static BackgroundImage auto(File file) throws IOException {
    File directory = file.getParentFile();
    return BackgroundImage.from(new File(directory, directory.getName() + ".bck.png"), DubendorfFrame._20190401);
  }

  public static File render(File directory) throws IOException {
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
        DubendorfFrame._20190401, //
        GokartPoseEvent.of(optional.get()).getPose());
    System.out.print("processing... ");
    OfflineLogPlayer.process(gokartLogInterface.file(), obstacleAggregate);
    System.out.print("finished");
    File image = new File(directory, directory.getName() + ".bck.png");
    ImageIO.write(bufferedImage, "png", image);
    return image;
  }
}
