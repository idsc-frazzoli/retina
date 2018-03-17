// code by jph
package ch.ethz.idsc.demo.jph.davis;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.davis.Aedat31PolarityListener;
import ch.ethz.idsc.retina.dev.davis.io.Aedat31PolarityEvent;
import ch.ethz.idsc.retina.util.TimedImageEvent;
import ch.ethz.idsc.retina.util.TimedImageListener;
import ch.ethz.idsc.retina.util.img.ImageCopy;

public class Aedat31PolarityImage implements Aedat31PolarityListener {
  // static final Color[] COLORS = new Color[] { Color.BLACK, Color.WHITE };
  static final Color[] COLORS = new Color[] { new Color(255, 0, 255), Color.GREEN };
  // ---
  private int time = 0;
  ImageCopy imageCopy = new ImageCopy();
  private BufferedImage bufferedImage;
  private final Graphics2D graphics;
  public final List<TimedImageListener> listeners = new LinkedList<>();

  public Aedat31PolarityImage() {
    bufferedImage = new BufferedImage(320, 264, BufferedImage.TYPE_3BYTE_BGR);
    graphics = bufferedImage.createGraphics();
    setBackground(bufferedImage);
  }

  @Override
  public void polarityEvent(Aedat31PolarityEvent aedat31PolarityEvent) {
    if (aedat31PolarityEvent.getTime_us() != time) {
      TimedImageEvent timedImageEvent = new TimedImageEvent(time, bufferedImage);
      listeners.forEach(listener -> listener.timedImage(timedImageEvent));
      // ---
      // graphics.setColor(Color.GRAY);
      // graphics.fillRect(0, 0, 320, 264);
      // System.out.println("image copy = " + imageCopy.get());
      graphics.drawImage(imageCopy.get(), 0, 0, null);
      time = aedat31PolarityEvent.getTime_us();
    }
    // TODO write to byte
    graphics.setColor(COLORS[aedat31PolarityEvent.i]);
    graphics.fillRect(aedat31PolarityEvent.x, aedat31PolarityEvent.y, 1, 1);
  }

  public void setBackground(BufferedImage bufferedImage) {
    // System.out.println("update" + bufferedImage);
    imageCopy.update(bufferedImage);
  }
}
