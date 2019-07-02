// code by mg
package ch.ethz.idsc.retina.app.slam.vis;

import java.io.File;

import ch.ethz.idsc.retina.app.SaveFrame;
import ch.ethz.idsc.retina.app.slam.SlamFileLocations;
import ch.ethz.idsc.retina.app.slam.config.SlamDvsConfig;

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
      SaveFrame.of(slamMapFrames[0].getFrame(), parentFilePath, logFilename, currentTimeStamp * 1E-3, imageCount);
    }
  }
}
