// code by mg
package ch.ethz.idsc.demo.mg.slam.config;

import java.io.File;
import java.io.IOException;

import ch.ethz.idsc.tensor.io.Import;
import ch.ethz.idsc.tensor.io.Primitives;

/* package */ enum StaticHelper {
  ;
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
