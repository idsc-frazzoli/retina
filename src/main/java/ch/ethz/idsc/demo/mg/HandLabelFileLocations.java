// code by mg
package ch.ethz.idsc.demo.mg;

import java.io.File;

import ch.ethz.idsc.owl.bot.util.UserHome;

public enum HandLabelFileLocations {
  ;
  /** @return directory of the images to be labelled. NOTE: only the images should be in that directory */
  public static File images() {
    return warningIfNotDirectory(UserHome.Pictures("handlabelimages"));
  }

  /** @param filename
   * @return file in directory containing the labels */
  public static File labels(String filename) {
    return new File(warningIfNotDirectory(UserHome.Pictures("handlabels")), filename);
  }

  /** @return directory for the GUI screenshots */
  public static File GUIVisualization() {
    return warningIfNotDirectory(UserHome.Pictures("dvs"));
  }

  private static File warningIfNotDirectory(File directory) {
    directory.mkdir();
    if (!directory.isDirectory())
      new RuntimeException("no directory: " + directory).printStackTrace();
    return directory;
  }
}
