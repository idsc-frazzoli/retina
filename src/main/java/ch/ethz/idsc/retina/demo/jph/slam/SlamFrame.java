// code by jph
package ch.ethz.idsc.retina.demo.jph.slam;

import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

public class SlamFrame implements OccupancyMapListener {
  private final static JLabel JLABEL = new JLabel();
  // ---
  private final JFrame jFrame = new JFrame();
  SlamComponent slamComponent = new SlamComponent();

  public SlamFrame() {
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setBounds(100, 50, 1100, 1050);
    jFrame.setContentPane(slamComponent.jComponent);
    jFrame.setVisible(true);
  }

  @Override
  public void occupancyMap(OccupancyMap occupancyMap) {
    BufferedImage bufferedImage = new BufferedImage(1024, 1024, BufferedImage.TYPE_BYTE_GRAY);
    bufferedImage.getGraphics().drawImage(occupancyMap.bufferedImage(), 0, 0, JLABEL);
    slamComponent.setImage(bufferedImage);
    slamComponent.setPose(occupancyMap.getPose()); // TODO make this safe from modification
    slamComponent.jComponent.repaint();
  }
}
