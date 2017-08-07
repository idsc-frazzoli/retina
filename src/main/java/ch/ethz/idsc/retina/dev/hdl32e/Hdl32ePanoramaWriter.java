// code by jph
package ch.ethz.idsc.retina.dev.hdl32e;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JLabel;

import ch.ethz.idsc.tensor.io.AnimationWriter;

public class Hdl32ePanoramaWriter implements Hdl32ePanoramaListener {
  private static final JLabel OBSERVER = new JLabel();
  private final AnimationWriter animationWriter;
  private final int width;
  private final BufferedImage image;
  private int frames = 0;

  public Hdl32ePanoramaWriter(File file, int period, int width) throws Exception {
    animationWriter = AnimationWriter.of(file, period);
    this.width = width;
    image = new BufferedImage(width, 64, BufferedImage.TYPE_INT_ARGB); // TODO MAGIC const
  }

  @Override
  public void panorama(Hdl32ePanorama hdl32ePanorama) {
    if (60 < frames && frames < 240) // FIXME not final
      try {
        BufferedImage subImage = hdl32ePanorama.distances().getSubimage(0, 0, hdl32ePanorama.getWidth(), 32);
        image.getGraphics().drawImage(subImage, 0, 0, width, 64, OBSERVER);
        animationWriter.append(image);
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    ++frames;
  }

  @Override
  public void close() throws Exception {
    animationWriter.close();
    System.out.println("closed gif");
  }
}
