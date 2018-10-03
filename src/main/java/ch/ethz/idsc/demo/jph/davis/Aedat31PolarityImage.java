// code by jph
package ch.ethz.idsc.demo.jph.davis;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.dev.davis.DavisDvsListener;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.util.TimedImageEvent;
import ch.ethz.idsc.retina.util.TimedImageListener;

public class Aedat31PolarityImage implements DavisDvsListener {
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
  public void davisDvs(DavisDvsEvent aedat31PolarityEvent) {
    // System.out.println(aedat31PolarityEvent.x+" "+aedat31PolarityEvent.y);
    if (aedat31PolarityEvent.time() != time) {
      time = aedat31PolarityEvent.time();
      ++packet;
    }
    if (packet >= packets) {
      // System.out.println("publish "+packet);
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
