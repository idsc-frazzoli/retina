// code by jph
package ch.ethz.idsc.demo.jph.lidar;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.TimeUnit;

import ch.ethz.idsc.retina.lidar.app.LidarPanorama;
import ch.ethz.idsc.retina.lidar.app.LidarPanoramaListener;
import ch.ethz.idsc.tensor.io.AnimationWriter;
import ch.ethz.idsc.tensor.io.GifAnimationWriter;

/** export of panorama to animated gif */
/* package */ class Hdl32ePanoramaWriter implements LidarPanoramaListener, AutoCloseable {
  private final AnimationWriter animationWriter;
  private final int width;
  private final BufferedImage image;
  private int frames = 0;

  public Hdl32ePanoramaWriter(File file, int period, int width) throws Exception {
    animationWriter = new GifAnimationWriter(file, period, TimeUnit.MILLISECONDS);
    this.width = width;
    image = new BufferedImage(width, 64, BufferedImage.TYPE_INT_ARGB); // magic const for one-time use
  }

  @Override
  public void lidarPanorama(LidarPanorama lidarPanorama) {
    if (60 < frames && frames < 240) // magic const for one-time use
      try {
        BufferedImage subImage = lidarPanorama.distances();
        image.createGraphics().drawImage(subImage, 0, 0, width, 64, null);
        animationWriter.write(image);
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
