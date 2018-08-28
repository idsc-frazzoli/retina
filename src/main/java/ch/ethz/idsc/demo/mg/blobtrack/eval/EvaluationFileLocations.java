// code by mg
package ch.ethz.idsc.demo.mg.blobtrack.eval;

import java.io.File;

import ch.ethz.idsc.owl.bot.util.UserHome;

// TODO MG adapt enum design as in MgEvaluationFolders with a single member function 
public enum EvaluationFileLocations {
  ;
  private static final File HANDLABEL_CSV = UserHome.Pictures("handlabels");
  private static final File ESTIMATED_CSV = UserHome.Pictures("estimatedlabels");
  private static final File EVALRESULTS_CSV = UserHome.Pictures("evalResults");
  // ---
  private static final File TESTING = UserHome.Pictures("testImages");

  /** @param filename without .csv extension
   * @return file in directory containing the handlabels */
  public static File handlabels(String filename) {
    return new File(StaticHelper.warningIfNotDirectory(HANDLABEL_CSV), filename + ".csv");
  }

  /** @param filename without .csv extension
   * @return file in directory containing the estimatedlabels */
  public static File estimatedlabels(String filename) {
    return new File(StaticHelper.warningIfNotDirectory(ESTIMATED_CSV), filename + ".csv");
  }

  /** @param filename without .csv extension
   * @return file in directory containing the estimatedlabels */
  public static File evalResults(String filename) {
    return new File(StaticHelper.warningIfNotDirectory(EVALRESULTS_CSV), filename + ".csv");
  }

  /** @return directory for the GUI screenshots */
  public static File testing() {
    return StaticHelper.warningIfNotDirectory(TESTING);
  }
}
