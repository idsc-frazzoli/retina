// code by mg
package ch.ethz.idsc.demo.mg.slam.vis;

import java.io.File;

import ch.ethz.idsc.demo.mg.slam.SlamFileLocations;
import ch.ethz.idsc.demo.mg.slam.config.SlamCoreConfig;
import ch.ethz.idsc.demo.mg.util.vis.VisGeneralUtil;

/** saves slamMapFrame objects using the time stamps provided by event stream */
/* package */ class SlamSaveFrame {
  private final SlamMapFrame[] slamMapFrames;
  private final String logFilename;
  private final File parentFilePath;
  private final boolean saveSlamFrame;
  // ---
  private int imageCount;

  public SlamSaveFrame(SlamCoreConfig slamConfig, SlamMapFrame[] slamMapFrames) {
    this.slamMapFrames = slamMapFrames;
    logFilename = slamConfig.davisConfig.logFilename();
    parentFilePath = SlamFileLocations.MAP_FRAMES.subfolder(logFilename);
    saveSlamFrame = slamConfig.saveSlamFrame;
  }

  public void saveFrame(int currentTimeStamp) {
    if (saveSlamFrame) {
      ++imageCount;
      VisGeneralUtil.saveFrame(slamMapFrames[1].getFrame(), parentFilePath, logFilename, currentTimeStamp * 1E-3, imageCount);
    }
  }
}
