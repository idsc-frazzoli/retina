// code by mg
package ch.ethz.idsc.demo.mg.slam;

import java.io.File;

import ch.ethz.idsc.owl.bot.util.UserHome;

public enum SlamFileLocations {
  ;
  private static final File MAP_FRAMES = UserHome.Pictures("slamFrames");

  /** @param subfolder name
   * @return directory of the subfolder. To be used to save slam map frames */
  public static File mapFrames(String subfolder) {
    warningIfNotDirectory(MAP_FRAMES);
    return warningIfNotDirectory(new File(MAP_FRAMES, subfolder));
  }

  private static File warningIfNotDirectory(File directory) {
    directory.mkdir();
    if (!directory.isDirectory())
      new RuntimeException("no directory: " + directory).printStackTrace();
    return directory;
  }
}
