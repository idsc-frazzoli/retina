// code by mg
package ch.ethz.idsc.demo.mg.slam.vis;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamFileLocations;
import ch.ethz.idsc.demo.mg.slam.algo.SlamProvider;
import ch.ethz.idsc.demo.mg.util.vis.VisGeneralUtil;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.retina.util.math.Magnitude;

/** wrapper class for the SLAM visualization */
public class SlamViewer {
  private final GokartPoseInterface gokartLidarPose;
  private final SlamProvider slamProvider;
  private final SlamMapGUI slamMapGUI;
  private final SlamMapFrame[] slamMapFrames;
  private final String logFilename;
  private final File parentFilePath;
  private final boolean lidarMappingMode;
  private final boolean saveSlamFrame;
  private final long visualizationInterval;
  private final long savingInterval;
  private int imageCount;
  // ---
  private final TimerTask visualizationTask;
  private final TimerTask saveFrameTask;

  public SlamViewer(SlamConfig slamConfig, SlamProvider slamProvider, GokartPoseInterface gokartLidarPose, Timer timer) {
    this.gokartLidarPose = gokartLidarPose;
    this.slamProvider = slamProvider;
    logFilename = slamConfig.davisConfig.logFilename();
    parentFilePath = SlamFileLocations.mapFrames(logFilename);
    lidarMappingMode = slamConfig.lidarMappingMode;
    saveSlamFrame = slamConfig.saveSlamFrame;
    visualizationInterval = Magnitude.MILLI_SECOND.toLong(slamConfig.visualizationInterval);
    savingInterval = Magnitude.MILLI_SECOND.toLong(slamConfig.savingInterval);
    slamMapGUI = new SlamMapGUI(slamConfig);
    slamMapFrames = new SlamMapFrame[3];
    for (int i = 0; i < slamMapFrames.length; i++)
      slamMapFrames[i] = new SlamMapFrame(slamConfig);
    visualizationTask = new TimerTask() {
      @Override
      public void run() {
        visualizationTask();
      }
    };
    saveFrameTask = new TimerTask() {
      @Override
      public void run() {
        saveFrameTask();
      }
    };
    timer.schedule(visualizationTask, 0, visualizationInterval);
    timer.schedule(saveFrameTask, 0, savingInterval);
  }

  private void visualizationTask() {
    if (slamProvider.getIsInitialized())
      slamMapGUI.setFrames(StaticHelper.constructFrames(slamMapFrames, slamProvider, gokartLidarPose, lidarMappingMode));
  }

  private void saveFrameTask() {
    if (saveSlamFrame && slamProvider.getIsInitialized()) {
      imageCount++;
      BufferedImage slamFrame = StaticHelper.constructFrames(slamMapFrames, slamProvider, gokartLidarPose, lidarMappingMode)[1];
      VisGeneralUtil.saveFrame(slamFrame, parentFilePath, logFilename, imageCount);
    }
  }
}
