// code by mg
package ch.ethz.idsc.demo.mg.slam.vis;

import java.util.Timer;
import java.util.TimerTask;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.retina.util.math.Magnitude;

public class SlamViewer {
  private final GokartPoseInterface gokartLidarPose;
  private final SlamContainer slamContainer;
  private final SlamMapFrame[] slamMapFrames;
  private final SlamMapGUI slamMapGUI;
  // ---
  private final Timer timer;
  private final TimerTask visualizationTask;
  private final TimerTask saveFrameTask;
  private final long visualizationInterval;
  private final long savingInterval;

  public SlamViewer(SlamConfig slamConfig, SlamContainer slamContainer, GokartPoseInterface gokartLidarPose) {
    this.gokartLidarPose = gokartLidarPose;
    this.slamContainer = slamContainer;
    slamMapGUI = new SlamMapGUI(slamConfig);
    slamMapFrames = new SlamMapFrame[3];
    for (int i = 0; i < slamMapFrames.length; i++)
      slamMapFrames[i] = new SlamMapFrame(slamConfig);
    // ---
    timer = new Timer();
    visualizationInterval = Magnitude.MILLI_SECOND.toLong(slamConfig.visualizationInterval);
    savingInterval = Magnitude.MILLI_SECOND.toLong(slamConfig.savingInterval);
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
  }

  private void visualizationTask() {
    if (slamContainer.getActive()) {
      slamMapGUI.setFrames(StaticHelper.constructFrames(slamMapFrames, slamContainer, gokartLidarPose));
    }
  }

  private void saveFrameTask() {
    // ---
  }
}
