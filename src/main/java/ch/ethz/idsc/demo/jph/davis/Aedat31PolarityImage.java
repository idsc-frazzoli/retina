// code by jph
package ch.ethz.idsc.demo.jph.davis;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.WritableRaster;
import java.util.LinkedList;
import java.util.List;

import ch.ethz.idsc.retina.davis.DavisDvsListener;
import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.util.img.TimedImageEvent;
import ch.ethz.idsc.retina.util.img.TimedImageListener;

public class Aedat31PolarityImage implements DavisDvsListener {
  private static final Color[] COLORS = new Color[] { //
      Color.MAGENTA, //
      Color.GREEN };
  // ---
  private final Color background;
  public final List<TimedImageListener> listeners = new LinkedList<>();
  private final BufferedImage bufferedImage;
  private final Graphics2D graphics;
  private final int packets;
  private final int[] data;
  // ---
  private int packet = 0;
  private int time = 0;

  public Aedat31PolarityImage(Color background, int packets) {
    this.background = background;
    bufferedImage = new BufferedImage(320, 264, BufferedImage.TYPE_INT_ARGB);
    WritableRaster writableRaster = bufferedImage.getRaster();
    DataBufferInt dataBufferByte = (DataBufferInt) writableRaster.getDataBuffer();
    data = dataBufferByte.getData();
    graphics = bufferedImage.createGraphics();
    this.packets = packets;
  }

  @Override
  public void davisDvs(DavisDvsEvent aedat31PolarityEvent) {
    if (aedat31PolarityEvent.time() != time) {
      time = aedat31PolarityEvent.time();
      ++packet;
    }
    if (packet >= packets) {
      packet = 0;
      TimedImageEvent timedImageEvent = new TimedImageEvent(time, bufferedImage);
      listeners.forEach(listener -> listener.timedImage(timedImageEvent));
      graphics.setColor(background);
      graphics.fillRect(0, 0, 320, 264);
      // Arrays.fill(data, background.getRGB());
    }
    graphics.setColor(COLORS[aedat31PolarityEvent.i]);
    graphics.fillRect(aedat31PolarityEvent.x, aedat31PolarityEvent.y, 1, 1);
  }
}
