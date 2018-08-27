// code by mg
package ch.ethz.idsc.demo.mg.slam.vis;

import java.io.File;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.demo.mg.slam.SlamFileLocations;
import ch.ethz.idsc.demo.mg.slam.algo.PeriodicSlamStep;
import ch.ethz.idsc.demo.mg.util.vis.VisGeneralUtil;

/** saves slamMapFrame objects using the timestamps provided by event stream */
/* package */ class SlamSaveFrame extends PeriodicSlamStep {
  private final SlamMapFrame[] slamMapFrames;
  private final String logFilename;
  private final File parentFilePath;
  private final boolean saveSlamFrame;
  // ---
  private int imageCount;

  public SlamSaveFrame(SlamConfig slamConfig, SlamContainer slamContainer, SlamMapFrame[] slamMapFrames) {
    super(slamContainer, slamConfig.savingInterval);
    this.slamMapFrames = slamMapFrames;
    logFilename = slamConfig.davisConfig.logFilename();
    parentFilePath = SlamFileLocations.mapFrames(logFilename);
    saveSlamFrame = slamConfig.saveSlamFrame;
  }

  @Override // from PeriodicSlamStep
  protected void periodicTask(int currentTimeStamp, int lastComputationTimeStamp) {
    if (saveSlamFrame) {
      ++imageCount;
      VisGeneralUtil.saveFrame(slamMapFrames[0].getFrame(), parentFilePath, logFilename, currentTimeStamp * 1E-3, imageCount);
    }
  }
}
