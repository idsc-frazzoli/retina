// code by mg
package ch.ethz.idsc.demo.mg.slam.vis;

import java.io.File;

import ch.ethz.idsc.demo.mg.slam.SlamFileLocations;
import ch.ethz.idsc.demo.mg.slam.config.SlamDvsConfig;
import ch.ethz.idsc.demo.mg.util.vis.VisGeneralUtil;

/** saves slamMapFrame objects using the time stamps provided by event stream */
/* package */ class SlamSaveFrame {
  private final String logFilename = SlamDvsConfig.eventCamera.slamCoreConfig.dvsConfig.logFilename();
  private final File parentFilePath = SlamFileLocations.MAP_FRAMES.subfolder(logFilename);
  private final boolean saveSlamFrame = SlamDvsConfig.eventCamera.slamCoreConfig.saveSlamFrame;
  private final SlamMapFrame[] slamMapFrames;
  // ---
  private int imageCount;

  public SlamSaveFrame(SlamMapFrame[] slamMapFrames) {
    this.slamMapFrames = slamMapFrames;
  }

  public void saveFrame(int currentTimeStamp) {
    if (saveSlamFrame) {
      ++imageCount;
      VisGeneralUtil.saveFrame(slamMapFrames[0].getFrame(), parentFilePath, logFilename, currentTimeStamp * 1E-3, imageCount);
    }
  }
}
