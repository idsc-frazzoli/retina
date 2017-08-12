// code by jph
package ch.ethz.idsc.retina.davis.io.txt;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import javax.imageio.ImageIO;

import ch.ethz.idsc.retina.core.ColumnTimedImageListener;
import ch.ethz.idsc.retina.davis.io.DavisExportControl;

public class DavisPngImageWriter implements ColumnTimedImageListener, AutoCloseable {
  private final File directory;
  private final DavisExportControl exportControl;
  private final BufferedWriter bufferedWriterBeg;
  private final BufferedWriter bufferedWriterEnd;
  private int count = 0;

  /** @param directory base
   * @throws Exception */
  public DavisPngImageWriter(File directory, DavisExportControl exportControl) throws Exception {
    this.directory = directory;
    this.exportControl = exportControl;
    File images = new File(directory, "images");
    images.mkdir();
    bufferedWriterBeg = new BufferedWriter(new FileWriter(new File(directory, "images_begin.txt")));
    bufferedWriterEnd = new BufferedWriter(new FileWriter(new File(directory, "images.txt")));
  }

  @Override
  public void image(int[] time, BufferedImage bufferedImage, boolean isComplete) {
    if (exportControl.isActive()) {
      try {
        final String string = String.format("images/frame_%08d.png", count);
        File file = new File(directory, string);
        ImageIO.write(bufferedImage, "png", file);
        // ---
        {
          int selected = time[0];
          final double stamp = exportControl.mapTime(selected) * 1e-6;
          bufferedWriterBeg.write(String.format("%.6f %s\n", stamp, string));
        }
        {
          int selected = time[time.length - 1];
          final double stamp = exportControl.mapTime(selected) * 1e-6;
          bufferedWriterEnd.write(String.format("%.6f %s\n", stamp, string));
        }
      } catch (Exception exception) {
        exception.printStackTrace();
      }
      ++count;
    } else {
      System.out.println("skip image export");
    }
  }

  @Override
  public void close() throws Exception {
    bufferedWriterBeg.close();
    bufferedWriterEnd.close();
  }
}
