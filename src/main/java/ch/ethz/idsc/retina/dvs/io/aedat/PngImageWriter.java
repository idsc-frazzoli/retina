// code by jph
package ch.ethz.idsc.retina.dvs.io.aedat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import ch.ethz.idsc.retina.dev.davis240c.DavisImageListener;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Export;

public class PngImageWriter implements DavisImageListener, AutoCloseable {
  private final File directory;
  private final BufferedWriter bufferedWriter;
  private int count = 0;

  /** @param directory base
   * @throws Exception */
  public PngImageWriter(File directory) throws Exception {
    this.directory = directory;
    File images = new File(directory, "images");
    images.mkdir();
    bufferedWriter = new BufferedWriter(new FileWriter(new File(directory, "images.txt")));
  }

  @Override
  public void image(int time, Tensor image) {
    try {
      final String string = String.format("images/frame_%08d.png", count);
      bufferedWriter.write(String.format("%.6f %s\n", time * 1e-6, string));
      Export.of(new File(directory, string), image);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    ++count;
  }

  @Override
  public void close() throws Exception {
    bufferedWriter.close();
  }
}
