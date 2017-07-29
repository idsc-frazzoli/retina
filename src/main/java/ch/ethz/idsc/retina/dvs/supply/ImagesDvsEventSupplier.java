// code by jph
package ch.ethz.idsc.retina.dvs.supply;

import java.awt.Dimension;
import java.io.File;
import java.util.Arrays;

import ch.ethz.idsc.retina.dvs.core.DvsEvent;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Import;

public class ImagesDvsEventSupplier implements DvsEventSupplier {
  private final Dimension dimension;
  private final File[] files;
  private final FrameDvsEventSupplier framesDvsEventSupplier;
  private int file_index = 0;
  private long time_us = 0;
  // private final long delta = 44065;
  private final long delta = 3_000; // TODO magic const
  public int limit = Integer.MAX_VALUE;

  public ImagesDvsEventSupplier(File dir, Dimension dimension) {
    files = dir.listFiles();
    Arrays.sort(files);
    this.dimension = dimension;
    // ---
    framesDvsEventSupplier = new InterpolatedFrameDvsEventSupplier(dimension);
  }

  @Override
  public DvsEvent next() throws Exception {
    while (framesDvsEventSupplier.isEmpty()) {
      File file = files[file_index];
      ++file_index;
      Tensor gry = Import.of(file);
      framesDvsEventSupplier.handle( //
          new TimedFrame(time_us, gry.get(Tensor.ALL, Tensor.ALL, 0)));
      time_us += delta;
      // System.out.println("read file " + file);
      if (limit < file_index)
        throw new RuntimeException();
    }
    return framesDvsEventSupplier.next();
  }

  @Override
  public Dimension dimension() {
    return dimension;
  }
}
