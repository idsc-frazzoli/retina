// code by mg
package ch.ethz.idsc.retina.util.io;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.DoubleStream;

import ch.ethz.idsc.tensor.io.Import;
import ch.ethz.idsc.tensor.io.Primitives;

/** utility to save/load primite arrays */
public enum PrimitivesIO {
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

  /** @param file
   * @return */
  public static double[] loadFromCSV(File file) {
    try {
      return Primitives.toDoubleArray(Import.of(file));
    } catch (IOException exception) {
      exception.printStackTrace();
    }
    return null;
  }
}
