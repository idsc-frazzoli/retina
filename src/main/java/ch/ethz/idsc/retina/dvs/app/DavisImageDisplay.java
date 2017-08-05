// code by jph
package ch.ethz.idsc.retina.dvs.app;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.dev.davis._240c.TimedImageListener;

public class DavisImageDisplay implements TimedImageListener {
  private final JLabel jLabel = new JLabel();
  private BufferedImage bufferedImage = null;
  private final JFrame jFrame = new JFrame();
  public final JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics g) {
      if (Objects.nonNull(bufferedImage))
        g.drawImage(bufferedImage, 0, 0, jLabel);
    }
  };

  public DavisImageDisplay() {
    jFrame.setBounds(100, 100, 400, 400);
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setContentPane(jComponent);
    jFrame.setVisible(true);
  }

  @Override
  public void image(int time, BufferedImage bufferedImage) {
    this.bufferedImage = bufferedImage;
    jComponent.repaint();
    try {
      Thread.sleep(1); // TODO IMAGE(...) SHOULD NEVER BLOCK -> display has to happen in different thread
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void close() {
    jFrame.setVisible(false);
    jFrame.dispose();
  }
}
