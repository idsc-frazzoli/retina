// code by mg
package ch.ethz.idsc.demo.mg;

import java.io.File;

import ch.ethz.idsc.owl.bot.util.UserHome;

public enum HandLabelFileLocations {
  ;
  private static final File HANDLABEL_IMAGES = UserHome.Pictures("handlabelimages");

  /** @param subfolder name
   * @return directory of the images to be labeled. NOTE: only the images should be in that directory */
  public static File images(String subfolder) {
    warningIfNotDirectory(HANDLABEL_IMAGES);
    return warningIfNotDirectory(new File(HANDLABEL_IMAGES, subfolder));
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
