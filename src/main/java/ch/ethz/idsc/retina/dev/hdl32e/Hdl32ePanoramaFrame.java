// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.img.ImageResize;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.red.Max;
import ch.ethz.idsc.tensor.red.Min;

public class Hdl32ePanoramaFrame implements Hdl32ePanoramaListener {
  public static final int SCALE_Y = 3;
  public final JFrame jFrame = new JFrame();
  private Hdl32ePanorama hdl32ePanorama;
  JComponent jComponent = new JComponent() {
    @Override
    protected void paintComponent(Graphics g) {
      Graphics2D graphics = (Graphics2D) g;
      Hdl32ePanorama hdl32ePanoramaRef = hdl32ePanorama;
      {
        Scalar max = hdl32ePanoramaRef.distances.flatten(-1).reduce(Max::of).get().Get();
        Scalar fac = RealScalar.of(255.0).divide(max);
        Tensor image = hdl32ePanoramaRef.distances.map(s -> max.subtract(s).multiply(fac));
        image = ImageResize.nearest(image, 1, SCALE_Y);
        BufferedImage bufferedImage = ImageFormat.of(image);
        graphics.drawImage(bufferedImage, 0, 0, jFrame);
      }
      {
        Tensor image = hdl32ePanoramaRef.intensity.map(s -> Min.of(RealScalar.of(255), s.multiply(RealScalar.of(2))));
        image = ImageResize.nearest(image, 1, SCALE_Y);
        BufferedImage bufferedImage = ImageFormat.of(image);
        graphics.drawImage(bufferedImage, 0, 32 * SCALE_Y, jFrame);
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
