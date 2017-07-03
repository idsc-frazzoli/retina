// code by jph
package ch.ethz.idsc.retina.io.txt;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.StringTokenizer;

import ch.ethz.idsc.retina.core.DvsEvent;
import ch.ethz.idsc.retina.supply.DvsEventSupplier;

public class TxtFileSupplier implements DvsEventSupplier, AutoCloseable {
  private final BufferedReader bufferedReader;
  private final Dimension dimension;

  public TxtFileSupplier(File file, Dimension dimension) throws Exception {
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
