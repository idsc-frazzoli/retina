// code by jph
package ch.ethz.idsc.retina.dev.davis.io;

import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import javax.imageio.ImageIO;

import ch.ethz.idsc.retina.util.ColumnTimedImageListener;

/** the images are exported with timestamp of the first column,
 * i.e. the earliest available timestamp.
 * 
 * this is consistent with the logs provided by the Robotics and Perception Group
 * as verified by inspection, see http://rpg.ifi.uzh.ch/ */
public class DavisPngImageWriter implements ColumnTimedImageListener, AutoCloseable {
  private static final String EXTENSION = "png";
  // ---
  private final File directory;
  private final DavisExportControl davisExportControl;
  private final BufferedWriter bufferedWriter;
  private int count = 0;

  /** @param directory base
   * @throws Exception */
  public DavisPngImageWriter(File directory, DavisExportControl davisExportControl) throws Exception {
    this.directory = directory;
    this.davisExportControl = davisExportControl;
    File images = new File(directory, "images");
    images.mkdir();
    bufferedWriter = new BufferedWriter(new FileWriter(new File(directory, "images.txt")));
  }

  @Override
  public void image(int[] time, BufferedImage bufferedImage, boolean isComplete) {
    if (davisExportControl.isActive()) {
      try {
        final String string = String.format("images/frame_%08d.%s", count, EXTENSION);
        File file = new File(directory, string);
        ImageIO.write(bufferedImage, EXTENSION, file);
        // ---
        final int selected = time[0];
        final double stamp = davisExportControl.mapTime(selected) * 1e-6;
        bufferedWriter.write(String.format("%.6f %s\n", stamp, string));
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
    bufferedWriter.close();
  }
}
