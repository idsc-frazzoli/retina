// code by jph
package ch.ethz.idsc.demo.jph.davis;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.function.Consumer;

import ch.ethz.idsc.retina.davis.Aedat31FrameListener;
import ch.ethz.idsc.retina.davis.io.Aedat31FrameEvent;
import ch.ethz.idsc.retina.util.img.TimedImageEvent;
import ch.ethz.idsc.retina.util.img.TimedImageListener;

class SiliconEyeVideoFrame implements Aedat31FrameListener, TimedImageListener {
  static final int WIDTH = 320;
  static final int HEIGHT = 264;
  // ---
  static final Dimension DIMENSION = new Dimension(WIDTH * 2, HEIGHT);
  // ---
  private final Consumer<BufferedImage> consumer;
  final BufferedImage bufferedImage = //
      new BufferedImage(DIMENSION.width, DIMENSION.height, BufferedImage.TYPE_3BYTE_BGR);
  final Graphics2D graphics = bufferedImage.createGraphics();
  int count = 0;
  private Aedat31FrameEvent frameEvent;

  public SiliconEyeVideoFrame(Consumer<BufferedImage> consumer) {
    this.consumer = consumer;
  }

  @Override
  public void timedImage(TimedImageEvent timedImageEvent) {
    if (count % 1000 == 0)
      System.out.println("here " + count);
    graphics.drawImage(frameEvent.getBufferedImage(), WIDTH, 0, null);
    graphics.setColor(new Color(0, 0, 0, 128 + 32));
    graphics.fillRect(WIDTH, 0, WIDTH, HEIGHT);
    // graphics.drawImage(timedImageEvent.bufferedImage, WIDTH, 0, null);
    graphics.drawImage(timedImageEvent.bufferedImage, WIDTH, 0, null);
    String string = Integer.toString(timedImageEvent.time) + "[us]";
    graphics.setColor(Color.BLACK);
    graphics.drawString(string, WIDTH + 1, 12);
    graphics.setColor(Color.WHITE);
    graphics.drawString(string, WIDTH, 11);
    consumer.accept(bufferedImage);
    ++count;
  }

  @Override
  public void frameEvent(Aedat31FrameEvent aedat31FrameEvent) {
    frameEvent = aedat31FrameEvent;
    graphics.drawImage(aedat31FrameEvent.getBufferedImage(), 0, 0, null);
  }
}