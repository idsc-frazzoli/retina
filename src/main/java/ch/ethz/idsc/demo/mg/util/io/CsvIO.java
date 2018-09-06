package ch.ethz.idsc.demo.mg.util.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Export;

/** methods to save primitives to CSV files */
public enum CsvIO {
  ;
  private static final String COMMA_DELIMITER = ",";
  private static final String NEW_LINE = "\n";

  /** saves a List<double[]> object to a CSV file. Single values are separated by COMMA_DELIMITER,
   * the list elements are separated by NEW_LINE
   * 
   * @param file list is saved to that file
   * @param doubleList length of list and double array are arbitrary */
  // TODO MG test with evalUtil, should replace EvalUtil::saveToCSV
  public static void saveToCSV(File file, List<double[]> doubleList) {
    // TODO MG try the code below
    try {
      Export.of(file, Tensor.of(doubleList.stream().map(Tensors::vectorDouble)));
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    try (FileWriter writer = new FileWriter(file)) {
      for (int i = 0; i < doubleList.size(); ++i) {
        final double[] singleLine = doubleList.get(i);
        for (int j = 0; j < singleLine.length; j++) {
          writer.append(String.valueOf(singleLine[j]));
          writer.append(COMMA_DELIMITER); // TODO convention to omit last comma?
        }
        writer.append(NEW_LINE);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
