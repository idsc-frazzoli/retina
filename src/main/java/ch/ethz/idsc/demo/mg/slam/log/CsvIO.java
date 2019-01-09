// code by mg
package ch.ethz.idsc.demo.mg.slam.log;

import java.io.File;
import java.util.List;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Export;

/** methods to save primitives to CSV files
 * 
 * Hint: the use of Export.of("file.csv", tensor) is preferred */
/* package */ enum CsvIO {
  ;
  /** saves a List<double[]> object to a CSV file. Single values are separated by COMMA_DELIMITER,
   * the list elements are separated by NEW_LINE
   * 
   * @param file list is saved to that file
   * @param list length of list and double array are arbitrary */
  public static void saveToCSV(File file, List<double[]> list) {
    try {
      Export.of(file, Tensor.of(list.stream().map(Tensors::vectorDouble)));
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
