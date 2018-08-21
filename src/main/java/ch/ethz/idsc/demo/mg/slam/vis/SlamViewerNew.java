// code by mg
package ch.ethz.idsc.demo.mg.slam.vis;

import java.util.Timer;
import java.util.TimerTask;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.retina.util.math.Magnitude;

public class SlamViewerNew {
  private final GokartPoseInterface gokartLidarPose;
  private final SlamContainer slamContainer;
  private final SlamMapGUI slamMapGUI;
  private final SlamMapFrame[] slamMapFrames;
  // ---
  private final Timer timer;
  private final TimerTask visualizationTask;
  private final long visualizationInterval;

  public SlamViewerNew(SlamConfig slamConfig, SlamContainer slamContainer, GokartPoseInterface gokartLidarPose) {
    this.gokartLidarPose = gokartLidarPose;
    this.slamContainer = slamContainer;
    slamMapGUI = new SlamMapGUI(slamConfig);
    slamMapFrames = new SlamMapFrame[3];
    for (int i = 0; i < slamMapFrames.length; i++)
      slamMapFrames[i] = new SlamMapFrame(slamConfig);
    // ---
    timer = new Timer();
    visualizationTask = new TimerTask() {
      @Override
      public void run() {
        visualizationTask();
      }
    };
    visualizationInterval = Magnitude.MILLI_SECOND.toLong(slamConfig.visualizationInterval);
    timer.schedule(visualizationTask, 0, visualizationInterval);
  }

  private void visualizationTask() {
    if (slamContainer.getActive()) {
      slamMapGUI.setFrames(StaticHelper.constructFrames(slamMapFrames, slamContainer, gokartLidarPose));
    }
  }
}
