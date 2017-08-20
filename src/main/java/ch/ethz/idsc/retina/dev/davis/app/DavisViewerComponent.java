// code by jph
package ch.ethz.idsc.retina.dev.davis.app;

import java.awt.Color;
import java.awt.Font;
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

// TODO magic const
/* package */ class DavisViewerComponent {
  private static final JLabel JLABEL = new JLabel();
  private static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 8);
  // ---
  BufferedImage apsImage = null;
  private BufferedImage dvsImage = null;
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
        graphics.drawImage(apsImage, 0, 0, JLABEL);
        if (!isComplete)
          graphics.drawString("incomplete!", 0, 200);
      }
      {
        BufferedImage refImage = dvsImage;
        if (Objects.nonNull(refImage))
          graphics.drawImage(refImage, 240, 0, JLABEL);
      }
      if (Objects.nonNull(imuFrame)) {
        graphics.setColor(Color.GRAY);
        graphics.drawString( //
            String.format("%4.1f C", imuFrame.temperature), 70, 190);
        graphics.drawString( //
            imuFrame.accel().map(Round._2).toString(), 120, 190);
        graphics.drawString( //
            imuFrame.gyro().map(Round.FUNCTION).toString(), 260, 190);
      }
      {
        graphics.setColor(Color.GRAY);
        graphics.drawString(displayEventCount.toString(), 0, 200);
      }
      // ---
      graphics.setColor(Color.RED);
      graphics.drawString(String.format("%4.1f Hz", (1.0e9 / period)), 0, 190);
    }
  };
  private int dvsImageCount = 0;

  public void setDvsImage(BufferedImage bufferedImage) {
    BufferedImage dvsImage = new BufferedImage(240, 180, BufferedImage.TYPE_BYTE_GRAY);
    Graphics graphics = dvsImage.getGraphics();
    graphics.drawImage(bufferedImage, 0, 0, JLABEL);
    graphics.setColor(Color.WHITE);
    graphics.setFont(FONT);
    graphics.drawString("" + dvsImageCount, 0, 175);
    this.dvsImage = dvsImage;
    ++dvsImageCount;
  }
}
