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

public class Aedat31PolarityImage implements Aedat31PolarityListener {
  private static final Color[] COLORS = new Color[] { //
      new Color(255, 0, 255), //
      Color.GREEN };
  private static final int[] CLEAR = new int[320 * 264];
  // ---
  public final List<TimedImageListener> listeners = new LinkedList<>();
  private final BufferedImage bufferedImage;
  private final Graphics2D graphics;
  private final int packets;
  // ---
  private int packet = 0;
  private int time = 0;

  public Aedat31PolarityImage(int packets) {
    bufferedImage = new BufferedImage(320, 264, BufferedImage.TYPE_INT_ARGB);
    graphics = bufferedImage.createGraphics();
    this.packets = packets;
  }

  @Override
  public void polarityEvent(Aedat31PolarityEvent aedat31PolarityEvent) {
    if (aedat31PolarityEvent.getTime_us() != time) {
      time = aedat31PolarityEvent.getTime_us();
      ++packet;
    }
    if (packet == packets) {
      packet = 0;
      TimedImageEvent timedImageEvent = new TimedImageEvent(time, bufferedImage);
      listeners.forEach(listener -> listener.timedImage(timedImageEvent));
      bufferedImage.setRGB(0, 0, 320, 264, CLEAR, 0, 320);
    }
    // TODO write to byte
    graphics.setColor(COLORS[aedat31PolarityEvent.i]);
    graphics.fillRect(aedat31PolarityEvent.x, aedat31PolarityEvent.y, 1, 1);
  }
}
