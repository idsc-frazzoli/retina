// code by jph
package ch.ethz.idsc.retina.dev.davis.app;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.JLabel;

import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.util.Stopwatch;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ class DavisDefaultComponent {
  private final JLabel jLabel = new JLabel();
  BufferedImage apsImage = null;
  BufferedImage dvsImage = null;
  DavisImuFrame imuFrame = null;
  private final Stopwatch stopwatch = new Stopwatch();
  boolean isComplete;
  Tensor displayEventCount = Array.zeros(3);
  final JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics graphics) {
      long period = stopwatch.stop();
      stopwatch.start();
      if (Objects.nonNull(apsImage)) {
        graphics.drawImage(apsImage, 0, 0, jLabel);
        if (!isComplete)
          graphics.drawString("incomplete!", 0, 200);
      }
      if (Objects.nonNull(dvsImage)) {
        graphics.drawImage(dvsImage, 240, 0, jLabel);
      }
      // synchronized (bufferedImage) {
      // graphics.drawImage(bufferedImage, 240, 0, jLabel);
      // }
      if (Objects.nonNull(imuFrame)) {
        graphics.setColor(Color.GRAY);
        graphics.drawString( //
            String.format("%4.1f C", imuFrame.temperature), 70, 190);
        graphics.drawString( //
            imuFrame.accel().map(Round._2).toString(), 120, 190);
        graphics.drawString( //
            imuFrame.gyro().map(Round._2).toString(), 260, 190);
      }
      // if (Objects.nonNull(davisEventStatistics))
      {
        graphics.setColor(Color.GRAY);
        graphics.drawString(displayEventCount.toString(), 0, 200);
      }
      // ---
      graphics.setColor(Color.RED);
      graphics.drawString(String.format("%4.1f Hz", (1.0e9 / period)), 0, 190);
    }
  };
}
