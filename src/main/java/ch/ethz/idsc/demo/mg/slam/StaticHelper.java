// code by mg
package ch.ethz.idsc.demo.mg.slam;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.DoubleStream;

/* package */ enum StaticHelper {
  ;
  /** @param file
   * @param array */
  public static void saveToCSV(File file, double[] array) {
    try (PrintWriter printWriter = new PrintWriter(file)) {
      DoubleStream.of(array).forEach(printWriter::println);
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }
}
