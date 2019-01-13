// code by jph
package ch.ethz.idsc.retina.davis.app;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Objects;

import javax.swing.JComponent;

import ch.ethz.idsc.retina.util.img.ColumnTimedImage;
import ch.ethz.idsc.retina.util.img.ColumnTimedImageListener;
import ch.ethz.idsc.retina.util.img.ImageCopy;
import ch.ethz.idsc.retina.util.img.TimedImageEvent;
import ch.ethz.idsc.retina.util.img.TimedImageListener;
import ch.ethz.idsc.retina.util.time.IntervalClock;

public class DavisQuickComponent {
  private BufferedImage difImage = null;
  private ImageCopy imageCopy = new ImageCopy();
  private final IntervalClock intervalClock = new IntervalClock();
  public final ColumnTimedImageListener difListener = new ColumnTimedImageListener() {
    @Override
    public void columnTimedImage(ColumnTimedImage columnTimedImage) {
      difImage = columnTimedImage.bufferedImage;
    }
  };
  public final TimedImageListener dvsImageListener = new TimedImageListener() {
    @Override
    public void timedImage(TimedImageEvent timedImageEvent) {
      imageCopy.update(timedImageEvent.bufferedImage);
    }
  };
  public final JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics graphics) {
      drawComponent((Graphics2D) graphics);
    }
  };

  public void drawComponent(Graphics2D graphics) {
    Dimension dimension = jComponent.getSize();
    int width = dimension.width;
    int height = width * 180 / 240;
    if (Objects.nonNull(difImage))
      graphics.drawImage(difImage, 0, 0, width, height, null);
    if (imageCopy.hasValue())
      graphics.drawImage(imageCopy.get(), 0, height, width, height, null);
    // ---
    graphics.setColor(Color.RED);
    graphics.drawString(String.format("%4.1f Hz", intervalClock.hertz()), 0, 10);
  }
}
