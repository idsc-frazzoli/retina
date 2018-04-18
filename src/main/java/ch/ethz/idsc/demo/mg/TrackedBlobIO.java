package ch.ethz.idsc.demo.mg;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import ch.ethz.idsc.demo.mg.pipeline.TrackedBlob;

public enum TrackedBlobIO {
  ;
  // loads binary file and draws the ellipses accordingly
  public static List<List<TrackedBlob>> loadFeatures(String pathToFile) {
    List<List<TrackedBlob>> loadedList = null;
    try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(pathToFile))) {
      loadedList = (List<List<TrackedBlob>>) inputStream.readObject(); // gives a warning
    } catch (Exception e) {
      e.printStackTrace();
    }
    return loadedList;
  }

  // saves array to binary file
  public static void saveFeatures(String pathToFile, List<List<TrackedBlob>> labeledFeatures) {
    try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(pathToFile))) {
      outputStream.writeObject(labeledFeatures);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
