// code by jph
package ch.ethz.idsc.demo.mg.blobtrack.eval;

import java.io.File;

import ch.ethz.idsc.owl.bot.util.UserHome;

public enum MgEvaluationFolders {
  /** directory of the images to be labeled. NOTE: only the images should be in that directory */
  HANDLABEL(UserHome.Pictures("handlabelimages")), //
  /** directory where evaluated images are saved */
  EVALUATED(UserHome.Pictures("evaluatedimages")), //
  ;
  private final File folder;

  private MgEvaluationFolders(File folder) {
    this.folder = StaticHelper.warningIfNotDirectory(folder);
  }

  /** @param subfolder name
   * @return directory */
  public File subfolder(String subfolder) {
    return StaticHelper.warningIfNotDirectory(new File(folder, subfolder));
  }
}
