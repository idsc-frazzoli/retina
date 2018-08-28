// code by mg
package ch.ethz.idsc.demo.mg.slam.vis;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamFileLocations;
import ch.ethz.idsc.demo.mg.util.vis.VisGeneralUtil;
import ch.ethz.idsc.retina.util.math.Magnitude;

/** saves slamMapFrame objects */
// TODO implement offline saving using timeStamps provided by DavisDvsListener
/* package */ class SlamMapFrameSave {
  private final SlamMapFrame[] slamMapFrames;
  private final String logFilename;
  private final File parentFilePath;
  private final Timer timer;
  private final TimerTask saveFrameTask;
  private final long savingInterval;
  // ---
  private int imageCount;

  public SlamMapFrameSave(SlamConfig slamConfig, Timer timer, SlamMapFrame[] slamMapFrames) {
    this.slamMapFrames = slamMapFrames;
    logFilename = slamConfig.davisConfig.logFilename();
    parentFilePath = SlamFileLocations.mapFrames(logFilename);
    this.timer = timer;
    saveFrameTask = new TimerTask() {
      @Override
      public void run() {
        saveFrameTask();
      }
    };
    savingInterval = Magnitude.MILLI_SECOND.toLong(slamConfig.savingInterval);
    timer.schedule(saveFrameTask, 0, savingInterval);
  }

  private void saveFrameTask() {
    ++imageCount;
    VisGeneralUtil.saveFrame(slamMapFrames[0].getFrame(), parentFilePath, logFilename, imageCount);
  }
}
