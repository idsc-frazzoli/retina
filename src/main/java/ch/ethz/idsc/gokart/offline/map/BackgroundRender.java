package ch.ethz.idsc.gokart.offline.map;

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
import ch.ethz.idsc.gokart.offline.channel.GokartPoseChannel;
import ch.ethz.idsc.tensor.Tensor;

public enum BackgroundRender {
  ;
  /** @param lcm_log
   * @param dimension
   * @param model2pixel
   * @param image file
   * @throws IOException */
  public static void render(File lcm_log, Dimension dimension, Tensor model2pixel, File image) throws IOException {
    Optional<ByteBuffer> optional = FirstLogMessage.of(lcm_log, GokartPoseChannel.INSTANCE.channel());
    BufferedImage bufferedImage = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB);
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
}
