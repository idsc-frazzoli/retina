// code by mg
package ch.ethz.idsc.demo.mg.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.demo.mg.pipeline.ImageBlob;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Import;
import ch.ethz.idsc.tensor.io.Primitives;

// provides static functions to work with CSV files
public class CSVUtil {
  private static final String COMMA_DELIMITER = ",";
  private static final String NEW_LINE = "\n";

  /** saves a List<List<ImageBlob>> object to a CSV file.
   * 
   * @param file object is saved to that file
   * @param featureList
   * @param timeStamps timestamps at which features are available */
  public static void saveToCSV(File file, List<List<ImageBlob>> featureList, int[] timeStamps) {
    FileWriter writer = null;
    try {
      writer = new FileWriter(file);
      for (int i = 0; i < featureList.size(); i++) {
        final List<ImageBlob> blobs = featureList.get(i);
        for (int j = 0; j < featureList.get(i).size(); j++) {
          final ImageBlob imageBlob = blobs.get(j);
          writer.append(String.valueOf(timeStamps[i]));
          writer.append(COMMA_DELIMITER);
          writer.append(String.valueOf(imageBlob.getPos()[0]));
          writer.append(COMMA_DELIMITER);
          writer.append(String.valueOf(imageBlob.getPos()[1]));
          writer.append(COMMA_DELIMITER);
          writer.append(String.valueOf(imageBlob.getCovariance()[0][0]));
          writer.append(COMMA_DELIMITER);
          writer.append(String.valueOf(imageBlob.getCovariance()[1][1]));
          writer.append(COMMA_DELIMITER);
          writer.append(String.valueOf(imageBlob.getCovariance()[1][0]));
          writer.append(NEW_LINE);
        }
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

  /** saves a List<double[]> object to a CSV file.
   * 
   * @param file object is saved to that file
   * @param collectedResults */
  public static void saveToCSV(File file, List<double[]> collectedResults) {
    FileWriter writer = null;
    try {
      writer = new FileWriter(file);
      for (int i = 0; i < collectedResults.size(); i++) {
        final double[] singleResult = collectedResults.get(i);
        writer.append(String.valueOf(singleResult[0]));
        writer.append(COMMA_DELIMITER);
        writer.append(String.valueOf(singleResult[1]));
        writer.append(COMMA_DELIMITER);
        writer.append(String.valueOf(singleResult[2]));
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

  /** loads an object from CSV file that was previously saved with saveToCSV fct. Returns null in case of
   * IOException. The ground truth timestamps need to be provided as well.
   * 
   * @param file object is loaded from that file
   * @param timeStamps timestamps of ground truth
   * @return List<List<ImageBlob>> object */
  public static List<List<ImageBlob>> loadFromCSV(File file, int[] timeStamps) {
    // set up empty list
    List<List<ImageBlob>> extractedFeatures = new ArrayList<>(timeStamps.length);
    for (int i = 0; i < timeStamps.length; i++)
      extractedFeatures.add(new ArrayList<>());
    try {
      Tensor inputTensor = Import.of(file);
      for (Tensor row : inputTensor) {
        int timestamp = row.Get(0).number().intValue();
        int index = Arrays.binarySearch(timeStamps, timestamp);
        float[] pos = Primitives.toFloatArray(row.extract(1, 3));
        double[][] cov = new double[][] { //
            { row.Get(3).number().doubleValue(), row.Get(5).number().doubleValue() },
            { row.Get(5).number().doubleValue(), row.Get(4).number().doubleValue() } };
        extractedFeatures.get(index).add(new ImageBlob(pos, cov, timestamp, true));
      }
      return extractedFeatures;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  /** load the timestamps from a CSV file previously saved with saveToCSV fct. Returns null in case of
   * IOException. Should only be used to load ground truth timestamps
   * 
   * @param file timestamps are read from that file
   * @return timestamps object indicating when features are available */
  public static int[] getTimestampsFromCSV(File file) {
    try {
      return Import.of(file).stream().mapToInt(row -> row.Get(0).number().intValue()).distinct().toArray();
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}
