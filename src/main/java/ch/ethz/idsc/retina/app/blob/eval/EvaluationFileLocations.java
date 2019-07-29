// code by mg
package ch.ethz.idsc.retina.app.blob.eval;

import java.io.File;

import ch.ethz.idsc.tensor.io.HomeDirectory;

public enum EvaluationFileLocations {
  HANDLABEL_CSV(HomeDirectory.Pictures("handlabels")), //
  ESTIMATED_CSV(HomeDirectory.Pictures("estimatedlabels")), //
  EVALRESULTS_CSV(HomeDirectory.Pictures("evalResults")), //
  TESTING(HomeDirectory.Pictures("testImages")), //
  ;
  // ---
  private final File folder;

  private EvaluationFileLocations(File folder) {
    this.folder = StaticHelper.warningIfNotDirectory(folder);
  }

  /** @param subfolder name
   * @return directory */
  public File subfolder(String subfolder) {
    return StaticHelper.warningIfNotDirectory(new File(folder, subfolder + ".csv"));
  }
}
