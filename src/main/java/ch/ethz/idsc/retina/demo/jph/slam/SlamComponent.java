// code by jph
package ch.ethz.idsc.retina.demo.jph.slam;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JLabel;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;
import ch.ethz.idsc.tensor.mat.Inverse;

public class SlamComponent {
  private final static JLabel JLABEL = new JLabel();
  // ---
  private BufferedImage bufferedImage;
  private Tensor pose = IdentityMatrix.of(3);
  final JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics graphics) {
      graphics.drawImage(bufferedImage, 0, 0, JLABEL);
      { // draw pose
        int x = 512 + pose.Get(0, 2).multiply(OccupancyMap.M2PIX).number().intValue();
        int y = 512 - pose.Get(1, 2).multiply(OccupancyMap.M2PIX).number().intValue();
        graphics.setColor(Color.GREEN);
        graphics.drawRect(x - 1, y - 1, 3, 3);
      }
    }
  };

  public void setImage(BufferedImage bufferedImage) {
    this.bufferedImage = bufferedImage;
  }

  public void setPose(Tensor pose) {
    this.pose = Inverse.of(pose);
  }
}
