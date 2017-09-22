// code by jph
package ch.ethz.idsc.retina.dev.davis.app;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Objects;

import javax.swing.JComponent;

import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.util.IntRange;
import ch.ethz.idsc.retina.util.IntervalClock;
import ch.ethz.idsc.retina.util.gui.BufferedImageCopy;
import ch.ethz.idsc.tensor.sca.Round;

// TODO magic const
/* package */ class DavisViewerComponent {
  private static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 8);
  // ---
  BufferedImage apsImage = null;
  BufferedImage rstImage = null;
  // private BufferedImage dvsImage = null;
  private BufferedImageCopy bufferedImageCopy = new BufferedImageCopy();
  DavisImuFrame imuFrame = null;
  private final IntervalClock intervalClock = new IntervalClock();
  boolean isComplete;
  int frame_duration = -1;
  int reset_duration = -1;
  DavisTallyEvent davisTallyEvent;
  // Tensor displayEventCount = Array.zeros(3);
  final JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics graphics) {
      if (Objects.nonNull(rstImage)) {
        graphics.drawImage(rstImage, 0 * 240, 0, null);
        if (!isComplete)
          graphics.drawString("incomplete!", 0, 200);
      }
      if (Objects.nonNull(apsImage)) {
        graphics.drawImage(apsImage, 1 * 240, 0, null);
        if (!isComplete)
          graphics.drawString("incomplete!", 0, 200);
      }
      if (bufferedImageCopy.hasValue())
        graphics.drawImage(bufferedImageCopy.get(), 2 * 240, 0, null);
      // ---
      if (Objects.nonNull(davisTallyEvent)) {
        DavisTallyEvent dte = davisTallyEvent;
        final int baseline_y = getSize().height - 20;
        graphics.setColor(Color.LIGHT_GRAY);
        for (int h = 15; h < 100; h += 15) {
          double blub = Math.exp(h * 0.1) - 1;
          graphics.fillRect(0, baseline_y - h, dte.binLast, 1);
          graphics.drawString("" + Math.round(blub), dte.binLast, baseline_y - h);
        }
        graphics.setColor(Color.BLUE);
        for (int index = 0; index < dte.binLast; ++index) {
          int height = (int) Math.round(Math.log(dte.bin[index] + 1) * 10);
          graphics.fillRect(index, baseline_y - height, 1, height);
        }
        drawBar(graphics, baseline_y, dte.resetRange, Color.RED, "RST");
        drawBar(graphics, baseline_y, dte.imageRange, Color.GREEN, "SIG");
        graphics.setColor(Color.GRAY);
        graphics.drawString(dte.getDurationUs() + " [us]", dte.binLast, baseline_y);
      }
      if (Objects.nonNull(imuFrame)) {
        graphics.setColor(Color.GRAY);
        graphics.drawString( //
            String.format("%4.1f C", imuFrame.temperature), 70, 180 + 12 * 1);
        graphics.drawString( //
            imuFrame.accel().map(Round._2) + "[m*s^-2]", 0, 180 + 12 * 2);
        graphics.drawString( //
            imuFrame.gyro().map(Round._2) + "[rad*s^-1]", 0, 180 + 12 * 3);
      }
      {
        // graphics.setColor(Color.GRAY);
        graphics.drawString(frame_duration + " " + reset_duration, 0, 180 + 12 * 4);
      }
      // ---
      graphics.setColor(Color.RED);
      graphics.drawString(String.format("%4.1f Hz", intervalClock.hertz()), 0, 190);
    }
  };

  private void drawBar(Graphics graphics, int y, IntRange intRange, Color color, String label) {
    if (Objects.nonNull(intRange)) {
      graphics.setColor(color);
      graphics.fillRect(intRange.min, y + 1, intRange.getWidth(), 2);
      graphics.setColor(Color.GRAY);
      graphics.drawString(label, intRange.min, y + 12);
    }
  }

  public void setDvsImage(BufferedImage bufferedImage) {
    bufferedImageCopy.update(bufferedImage);
  }
}
