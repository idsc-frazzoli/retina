// code by jph
package ch.ethz.idsc.retina.dvs.app;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.dev.davis.ColumnTimedImageListener;
import ch.ethz.idsc.retina.dev.davis.TimedImageListener;

// TODO redraw thread is independent of sync signal of images...!
public class DefaultDavisDisplay implements Runnable {
  private final JLabel jLabel = new JLabel();
  private BufferedImage bufferedImage = null;
  private BufferedImage dvsImage = null;
  private final JFrame jFrame = new JFrame();
  private boolean isLaunched = true;
  long repaint_tic = System.nanoTime();
  private boolean isComplete;
  public final JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics graphics) {
      repaint_tic = System.nanoTime();
      if (Objects.nonNull(bufferedImage)) {
        graphics.drawImage(bufferedImage, 0, 0, jLabel);
        if (!isComplete)
          graphics.drawString("incomplete!", 0, 200);
      }
      if (Objects.nonNull(dvsImage))
        graphics.drawImage(dvsImage, 240, 0, jLabel);
    }
  };

  public DefaultDavisDisplay() {
    jFrame.setBounds(100, 100, 500, 200);
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setContentPane(jComponent);
    jFrame.setVisible(true);
    new Thread(this).start();
  }

  @Override
  public void run() {
    while (isLaunched) {
      long toc = System.nanoTime() - repaint_tic;
      if (100e6 < toc) {
        System.err.println("image data lag");
        jComponent.repaint();
      }
      try {
        Thread.sleep(100);
      } catch (InterruptedException exception) {
        exception.printStackTrace();
      }
    }
  }

  public final ColumnTimedImageListener apsRenderer = new ColumnTimedImageListener() {
    @Override
    public void image(int[] time, BufferedImage bufferedImage, boolean isComplete) {
      if (!isComplete)
        System.err.println("image incomplete");
      setBufferedImage(bufferedImage, isComplete);
      jComponent.repaint();
    }
  };
  public final TimedImageListener dvsRenderer = new TimedImageListener() {
    @Override
    public void image(int time, BufferedImage bufferedImage) {
      setDvsImage(bufferedImage);
      jComponent.repaint();
    }
  };

  public void close() {
    isLaunched = false;
    jFrame.setVisible(false);
    jFrame.dispose();
  }

  void setDvsImage(BufferedImage bufferedImage) {
    this.dvsImage = bufferedImage;
  }

  private void setBufferedImage(BufferedImage bufferedImage, boolean isComplete) {
    this.bufferedImage = bufferedImage;
    this.isComplete = isComplete;
  }
}
