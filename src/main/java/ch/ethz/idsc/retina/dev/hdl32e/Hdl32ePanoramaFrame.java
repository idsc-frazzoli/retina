// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.Objects;
import java.util.stream.IntStream;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ColorFormat;

public class Hdl32ePanoramaFrame implements Hdl32ePanoramaListener {
  public static final int SCALE_Y = 3;
  // ---
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
        if (false) {
          // .getDataBuffer();
          DataBufferByte dataBufferByte = (DataBufferByte) hdl32ePanoramaRef.distances().getRaster().getDataBuffer();
          byte[] bytes = dataBufferByte.getData();
          int[] array = IntStream.range(0, bytes.length) //
              .mapToObj(i -> RealScalar.of((bytes[i] & 0xff) / 255.0)) //
              .map(ColorDataGradients.HSLUV) //
              .mapToInt(ColorFormat::toInt) //
              .toArray();
          // null; // tensor.flatten(1).mapToInt(ColorFormat::toInt).toArray();
          colorImage.setRGB(0, 0, 2048, 32, array, 0, 2048);
          graphics.drawImage(colorImage, 0, 16 + 2 * height, 2048, height, jFrame);
        }
      }
    }
  };
  BufferedImage colorImage;

  public Hdl32ePanoramaFrame() {
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setBounds(100, 100, 800, 800);
    jFrame.setContentPane(jComponent);
    jFrame.setVisible(true);
    colorImage = new BufferedImage(2048, 32, BufferedImage.TYPE_INT_ARGB);
  }

  @Override
  public void panorama(Hdl32ePanorama hdl32ePanorama) {
    this.hdl32ePanorama = hdl32ePanorama;
    jComponent.repaint();
    try {
      Thread.sleep(10);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
