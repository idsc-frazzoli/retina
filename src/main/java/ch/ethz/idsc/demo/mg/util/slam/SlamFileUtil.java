// code by mg
package ch.ethz.idsc.demo.mg.util.slam;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.DoubleStream;

import ch.ethz.idsc.demo.mg.slam.MapProvider;
import ch.ethz.idsc.tensor.io.Import;
import ch.ethz.idsc.tensor.io.Primitives;

/** utility to save/load maps obtained from the SLAM algorithm */
public class SlamFileUtil {
  /** saves a {@link MapProvider} map array in a csv file. one value per line
   * 
   * @param file
   * @param map */
  public static void saveToCSV(File file, MapProvider map) {
    try (PrintWriter printWriter = new PrintWriter(file)) {
      DoubleStream.of(map.getMapArray()) //
          .forEach(printWriter::println);
    } catch (IOException exception) {
      exception.printStackTrace();
    }
  }

  /** loads a {@link MapProvider} map array form a csv file
   * 
   * @param file
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
