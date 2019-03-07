// code by jph
package ch.ethz.idsc.retina.dvs.supply;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.StringTokenizer;

import ch.ethz.idsc.retina.dvs.core.DvsEvent;

public class DvsTxtFileSupplier implements DvsEventSupplier, AutoCloseable {
  private final BufferedReader bufferedReader;
  private final Dimension dimension;

  public DvsTxtFileSupplier(File file, Dimension dimension) throws Exception {
    bufferedReader = new BufferedReader(new FileReader(file));
    this.dimension = dimension;
  }

  @Override
  public DvsEvent next() throws Exception {
    String line = bufferedReader.readLine();
    // System.out.println(line);
    StringTokenizer stringTokenizer = new StringTokenizer(line);
    long time_us = (long) (Double.parseDouble(stringTokenizer.nextToken()) * 1e6);
    int x = Integer.parseInt(stringTokenizer.nextToken()); // x
    int y = Integer.parseInt(stringTokenizer.nextToken()); // y
    int i = Integer.parseInt(stringTokenizer.nextToken()); // i
    if (x < 0 || y < 0 || dimension.width <= x || dimension.height <= y) {
      // System.out.println(new DvsEvent(time_us, x, y, i).toString());
      // System.out.println(String.form);
      throw new RuntimeException();
      // x=0;
      // y=0;
    }
    return new DvsEvent(time_us, x, y, i);
  }

  @Override
  public Dimension dimension() {
    return dimension;
  }

  @Override
  public void close() throws Exception {
    bufferedReader.close();
  }
}
