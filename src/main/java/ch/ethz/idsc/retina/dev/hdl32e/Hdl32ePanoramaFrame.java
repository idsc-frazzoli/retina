// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Objects;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class Hdl32ePanoramaFrame implements Hdl32ePanoramaListener {
  public static final int SCALE_Y = 3;
  public final JFrame jFrame = new JFrame();
  private Hdl32ePanorama hdl32ePanorama;
  JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics g) {
      Graphics2D graphics = (Graphics2D) g;
      Hdl32ePanorama hdl32ePanoramaRef = hdl32ePanorama;
      if (Objects.nonNull(hdl32ePanoramaRef)) {
        final int height = 32 * SCALE_Y;
        graphics.drawImage(hdl32ePanoramaRef.distances(), 0, 0, 2048, height, jFrame);
        graphics.drawImage(hdl32ePanoramaRef.intensity(), 0, 16 + height, 2048, height, jFrame);
      }
    }
  };

  public Hdl32ePanoramaFrame() {
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setBounds(100, 100, 800, 800);
    jFrame.setContentPane(jComponent);
    jFrame.setVisible(true);
  }

  @Override
  public void panorama(Hdl32ePanorama hdl32ePanorama) {
    this.hdl32ePanorama = hdl32ePanorama;
    jComponent.repaint();
    try {
      Thread.sleep(1);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
