package ch.ethz.idsc.demo.mg;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;

import ch.ethz.idsc.demo.mg.pipeline.ImageBlob;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.Import;

public enum ImageBlobIO {
  ;
  // loads binary file
  public static List<List<ImageBlob>> loadFeatures(File pathToFile) {
    List<List<ImageBlob>> loadedList = null;
    try {
      loadedList = Import.object(pathToFile);
    } catch (ClassNotFoundException e1) {
      e1.printStackTrace();
    } catch (IOException e1) {
      e1.printStackTrace();
    } catch (DataFormatException e1) {
      e1.printStackTrace();
    }
    return loadedList;
  }

  // saves array to binary file
  public static void saveFeatures(File pathToFile, List<List<ImageBlob>> labeledFeatures) {
    try {
      Export.object(pathToFile, labeledFeatures);
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }
}
