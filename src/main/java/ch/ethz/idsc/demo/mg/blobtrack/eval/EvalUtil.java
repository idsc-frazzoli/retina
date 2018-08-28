// code by mg
package ch.ethz.idsc.demo.mg.blobtrack.eval;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.demo.mg.blobtrack.ImageBlob;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Import;
import ch.ethz.idsc.tensor.io.Primitives;

/** provides static functions to work with CSV files */
/* package */ enum EvalUtil {
  ;
  private static final String COMMA_DELIMITER = ",";
  private static final String NEW_LINE = "\n";
  private static final int DEFAULT_BLOB_ID = 0;

  /** saves a List<List<ImageBlob>> object to a CSV file.
   * 
   * @param file object is saved to that file
   * @param featureList
   * @param timeStamps timestamps at which features are available */
  public static void saveToCSV(File file, List<List<ImageBlob>> featureList, int[] timeStamps) {
    try (FileWriter writer = new FileWriter(file)) {
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
    }
  }

  /** saves a List<double[]> object to a CSV file.
   * 
   * @param file object is saved to that file
   * @param collectedResults */
  public static void saveToCSV(File file, List<double[]> collectedResults) {
    try (FileWriter writer = new FileWriter(file)) {
      for (int i = 0; i < collectedResults.size(); ++i) {
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
    for (int i = 0; i < timeStamps.length; ++i)
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
        extractedFeatures.get(index).add(new ImageBlob(pos, cov, timestamp, true, DEFAULT_BLOB_ID));
      }
      return extractedFeatures;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /** load the timestamps from the hand-labeled images.
   * 
   * @param numberOfFiles number of hand-labeled images available
   * @param imagePrefix specifies hand-labeled folder location
   * @return timeStamps array with extracted timestamps */
  public static int[] getTimestampsFromImages(int numberOfFiles, String imagePrefix) {
    int[] timeStamps = new int[numberOfFiles];
    // get all filenames and sort
    String[] fileNames = MgEvaluationFolders.HANDLABEL.subfolder(imagePrefix).list();
    Arrays.sort(fileNames);
    for (int i = 0; i < numberOfFiles; ++i) {
      String fileName = fileNames[i];
      // remove file extension
      fileName = fileName.substring(0, fileName.lastIndexOf("."));
      String splitFileName[] = fileName.split("_");
      timeStamps[i] = Integer.parseInt(splitFileName[2]);
    }
    return timeStamps;
  }
}
