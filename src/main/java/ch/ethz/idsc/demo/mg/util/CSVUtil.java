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
  private static String COMMA_DELIMITER = ",";
  private static String NEW_LINE = "\n";;

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
        for (int j = 0; j < featureList.get(i).size(); j++) {
          writer.append(String.valueOf(timeStamps[i]));
          writer.append(COMMA_DELIMITER);
          writer.append(String.valueOf(featureList.get(i).get(j).getPos()[0]));
          writer.append(COMMA_DELIMITER);
          writer.append(String.valueOf(featureList.get(i).get(j).getPos()[1]));
          writer.append(COMMA_DELIMITER);
          writer.append(String.valueOf(featureList.get(i).get(j).getCovariance()[0][0]));
          writer.append(COMMA_DELIMITER);
          writer.append(String.valueOf(featureList.get(i).get(j).getCovariance()[1][1]));
          writer.append(COMMA_DELIMITER);
          writer.append(String.valueOf(featureList.get(i).get(j).getCovariance()[1][0]));
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

  /** loads an object from CSV file that was previously saved with saveToCSV fct. Returns null in case of
   *  IOException.
   * 
   * @param file object is loaded from that file
   * @return List<List<ImageBlob>> object*/
  public static List<List<ImageBlob>> loadFromCSV(File file) {
    // extract timestamps first
    int[] timeStamps = getTimestampsFromCSV(file);
    try {
      // set up empty list
      List<List<ImageBlob>> extractedFeatures = new ArrayList<>(timeStamps.length);
      for (int i = 0; i < timeStamps.length; i++) {
        List<ImageBlob> emptyList = new ArrayList<>();
        extractedFeatures.add(emptyList);
      }
      Tensor inputTensor = Import.of(file);
      for (Tensor row : inputTensor) {
        int timestamp = row.Get(0).number().intValue();
        int index = Arrays.binarySearch(timeStamps, timestamp);
        float[] pos = Primitives.toFloatArray(row.extract(1, 3));
        double[][] cov = new double[][] { { row.Get(3).number().doubleValue(), row.Get(5).number().doubleValue() },
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
   *  IOException.
   * 
   * @param file timestamps are read from that file
   * @return timestamps object indicating when features are available */
  public static int[] getTimestampsFromCSV(File file) {
    // use list because length is unknown
    List<Integer> timestampList = new ArrayList<>();
    try {
      Tensor inputTensor = Import.of(file);
      // initialize extractedTimestamps
      timestampList.add(inputTensor.get(0).Get(0).number().intValue());
      for (int i = 1; i < inputTensor.length(); i++) {
        if (inputTensor.get(i).Get(0).number().intValue() != timestampList.get(timestampList.size() - 1)) {
          timestampList.add(inputTensor.get(i).Get(0).number().intValue());
        }
      }
      // convert list to array
      int[] timeStamps = new int[timestampList.size()];
      for (int i = 0; i < timestampList.size(); i++) {
        timeStamps[i] = timestampList.get(i);
      }
      return timeStamps;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}
