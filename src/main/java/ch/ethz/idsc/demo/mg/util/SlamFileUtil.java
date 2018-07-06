// code by mg
package ch.ethz.idsc.demo.mg.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import ch.ethz.idsc.demo.mg.slam.MapProvider;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Import;

// utility to save/load maps obtained from the SLAM algorithm
public class SlamFileUtil {
  private static final String NEW_LINE = "\n";

  /** saves a {@link MapProvider} map array in a csv file. one value per line
   * 
   * @param file
   * @param map */
  public static void saveToCSV(File file, MapProvider map) {
    double[] mapArray = map.getMapArray();
    FileWriter writer = null;
    try {
      writer = new FileWriter(file);
      for (int i = 0; i < mapArray.length; i++) {
        writer.append(String.valueOf(mapArray[i]));
        writer.append(NEW_LINE);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        writer.flush();
        writer.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /** loads a {@link MapProvider} map array form a csv file
   * 
   * @param file
   * @param map */
  public static void loadFromCSV(File file, double[] map) {
    try {
      Tensor inputTensor = Import.of(file);
      if (inputTensor.length() != map.length)
        System.out.println("FATAL @loadFromCSV: array not same length");
      int i = 0;
      for (Tensor row : inputTensor) {
        double mapValue = row.Get(0).number().doubleValue();
        map[i] = mapValue;
        i++;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
