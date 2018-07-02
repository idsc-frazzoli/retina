// code by mg
package ch.ethz.idsc.demo.mg.slam;

import java.io.File;

import ch.ethz.idsc.owl.bot.util.UserHome;

public enum SlamFileLocations {
  ;
  private static final File MAP_FRAMES = UserHome.Pictures("slamFrames");
  private static final File RECORDED_MAP = UserHome.Pictures("slamMaps");

  /** @param subfolder name
   * @return directory of the subfolder. To be used to save slam map frames */
  public static File mapFrames(String subfolder) {
    warningIfNotDirectory(MAP_FRAMES);
    return warningIfNotDirectory(new File(MAP_FRAMES, subfolder));
  }

  /** @param filename without .csv extension
   * @return file in directory containing the recorded map values */
  public static File recordedMaps(String fileName) {
    fileName += ".csv";
    warningIfNotDirectory(RECORDED_MAP);
    return new File(warningIfNotDirectory(RECORDED_MAP), fileName);
  }

  private static File warningIfNotDirectory(File directory) {
    directory.mkdir();
    if (!directory.isDirectory())
      new RuntimeException("no directory: " + directory).printStackTrace();
    return directory;
  }
}
