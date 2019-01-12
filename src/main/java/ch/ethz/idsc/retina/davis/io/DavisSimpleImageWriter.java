// code by jph
package ch.ethz.idsc.retina.davis.io;

import java.io.File;

import javax.imageio.ImageIO;

import ch.ethz.idsc.retina.util.img.TimedImageEvent;
import ch.ethz.idsc.retina.util.img.TimedImageListener;

public class DavisSimpleImageWriter implements TimedImageListener {
  private final File directory;
  private final int limit;
  private final DavisExportControl exportControl;
  private int count = 0;

  /** @param directory base
   * @param limit
   * @throws Exception */
  public DavisSimpleImageWriter(File directory, int limit, DavisExportControl exportControl) {
    this.directory = directory;
    this.limit = limit;
    this.exportControl = exportControl;
  }

  @Override
  public void timedImage(TimedImageEvent timedImageEvent) {
    if (exportControl.isActive()) {
      if (limit < 0 || count < limit)
        try {
          final String string = String.format("%08d.png", count);
          File file = new File(directory, string);
          ImageIO.write(timedImageEvent.bufferedImage, "png", file);
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      ++count;
    }
  }
}
