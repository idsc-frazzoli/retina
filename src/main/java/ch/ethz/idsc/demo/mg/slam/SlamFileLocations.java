// code by mg
package ch.ethz.idsc.demo.mg.slam;

import java.io.File;

import ch.ethz.idsc.tensor.io.HomeDirectory;

public enum SlamFileLocations {
  MAP_FRAMES(HomeDirectory.Pictures("slamFrames")), //
  RECORDED_MAP(HomeDirectory.Pictures("slamMaps")), //
  OFFLINELOGS(HomeDirectory.Pictures("slamOfflineLogs")), //
  ;
  private final File folder;

  private SlamFileLocations(File folder) {
    this.folder = warningIfNotDirectory(folder);
  }

  /** @param subfolder name
   * @return directory */
  public File subfolder(String subfolder) {
    return warningIfNotDirectory(new File(folder, subfolder));
  }

  /** @param filename without .csv extension
   * @return file in folder with .csv extension */
  public File inFolder(String filename) {
    filename += ".csv";
    return new File(warningIfNotDirectory(folder), filename);
  }

  private static File warningIfNotDirectory(File directory) {
    directory.mkdir();
    if (!directory.isDirectory())
      new RuntimeException("no directory: " + directory).printStackTrace();
    return directory;
  }
}
