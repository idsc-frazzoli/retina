// code by jph
package ch.ethz.idsc.retina.davis.io;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import ch.ethz.idsc.retina.util.img.ColumnTimedImage;
import ch.ethz.idsc.retina.util.img.ColumnTimedImageListener;
import ch.ethz.idsc.retina.util.img.TimedImageEvent;
import ch.ethz.idsc.retina.util.img.TimedImageListener;
import ch.ethz.idsc.tensor.io.AnimationWriter;
import ch.ethz.idsc.tensor.io.GifAnimationWriter;

/**  */
public class DavisGifImageWriter implements ColumnTimedImageListener, TimedImageListener, AutoCloseable {
  private final AnimationWriter animationWriter;
  private final DavisExportControl davisExportControl;
  private int count = 0;

  /** @param directory base in which a sub directory "images" is created
   * @throws IOException */
  public DavisGifImageWriter(File file, int period, DavisExportControl davisExportControl) throws IOException {
    animationWriter = new GifAnimationWriter(file, period, TimeUnit.MILLISECONDS);
    this.davisExportControl = davisExportControl;
  }

  @Override
  public void columnTimedImage(ColumnTimedImage columnTimedImage) {
    image(columnTimedImage.bufferedImage);
  }

  @Override
  public void timedImage(TimedImageEvent timedImageEvent) {
    image(timedImageEvent.bufferedImage);
  }

  private void image(BufferedImage bufferedImage) {
    if (davisExportControl.isActive())
      try {
        animationWriter.write(bufferedImage);
        ++count;
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    else
      System.out.println("skip image export");
  }

  @Override
  public void close() throws Exception {
    animationWriter.close();
  }

  public int total_frames() {
    return count;
  }
}
