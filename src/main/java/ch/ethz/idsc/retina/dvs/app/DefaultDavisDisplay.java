// code by jph
package ch.ethz.idsc.retina.dvs.app;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.dev.davis.TimedImageListener;

// TODO redraw thread is independent of sync signal of images...!
public class DefaultDavisDisplay {
  private final JLabel jLabel = new JLabel();
  private BufferedImage bufferedImage = null;
  private BufferedImage dvsImage = null;
  private final JFrame jFrame = new JFrame();
  public final JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics g) {
      if (Objects.nonNull(bufferedImage))
        g.drawImage(bufferedImage, 0, 0, jLabel);
      if (Objects.nonNull(dvsImage))
        g.drawImage(dvsImage, 240, 0, jLabel);
    }
  };

  public DefaultDavisDisplay() {
    jFrame.setBounds(100, 100, 500, 200);
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setContentPane(jComponent);
    jFrame.setVisible(true);
  }

  public final TimedImageListener apsRenderer = new TimedImageListener() {
    @Override
    public void image(int time, BufferedImage bufferedImage) {
      setBufferedImage(bufferedImage);
      jComponent.repaint();
      try {
        Thread.sleep(1); // TODO IMAGE(...) SHOULD NEVER BLOCK -> display has to happen in different thread
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }
  };
  public final TimedImageListener dvsRenderer = new TimedImageListener() {
    @Override
    public void image(int time, BufferedImage bufferedImage) {
      setDvsImage(bufferedImage);
      jComponent.repaint();
      try {
        Thread.sleep(1); // TODO IMAGE(...) SHOULD NEVER BLOCK -> display has to happen in different thread
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    }
  };

  public void close() {
    jFrame.setVisible(false);
    jFrame.dispose();
  }

  void setDvsImage(BufferedImage bufferedImage) {
    this.dvsImage = bufferedImage;
  }

  void setBufferedImage(BufferedImage bufferedImage) {
    this.bufferedImage = bufferedImage;
  }
}
