// code by mg
package ch.ethz.idsc.demo.mg.slam.vis;

import java.util.Timer;
import java.util.TimerTask;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.demo.mg.slam.algo.PeriodicSlamStep;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.retina.util.math.Magnitude;

/** SLAM algorithm visualization wrapper. PeriodicSlamStep is implemented to have access to a time stamp for saving of frames */
public class SlamViewer extends PeriodicSlamStep {
  private final GokartPoseInterface gokartLidarPose;
  private final SlamMapFrame[] slamMapFrames;
  private final SlamMapGUI slamMapGUI;
  private final SlamSaveFrame slamSaveFrame;
  // ---
  private final Timer timer;
  private final TimerTask visualizationTask;
  private final long visualizationInterval;

  public SlamViewer(SlamConfig slamConfig, SlamContainer slamContainer, GokartPoseInterface gokartLidarPose) {
    super(slamContainer, slamConfig.savingInterval);
    this.gokartLidarPose = gokartLidarPose;
    slamMapGUI = new SlamMapGUI(slamConfig);
    slamMapFrames = new SlamMapFrame[2];
    for (int i = 0; i < slamMapFrames.length; i++)
      slamMapFrames[i] = new SlamMapFrame(slamConfig);
    // ---
    timer = new Timer();
    visualizationInterval = Magnitude.MILLI_SECOND.toLong(slamConfig.visualizationInterval);
    visualizationTask = new TimerTask() {
      @Override
      public void run() {
        visualizationTask();
      }
    };
    timer.schedule(visualizationTask, 0, visualizationInterval);
    slamSaveFrame = new SlamSaveFrame(slamConfig, slamMapFrames);
  }

  private void visualizationTask() {
    slamMapGUI.setFrames(StaticHelper.constructFrames(slamMapFrames, slamContainer, gokartLidarPose.getPose()));
  }

  @Override // from PeriodicSlamStep
  protected void periodicTask(int currentTimeStamp, int lastComputationTimeStamp) {
    slamSaveFrame.saveFrame(currentTimeStamp);
  }
}
