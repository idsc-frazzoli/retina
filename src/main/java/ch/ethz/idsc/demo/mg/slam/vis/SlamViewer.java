// code by mg
package ch.ethz.idsc.demo.mg.slam.vis;

import java.util.Timer;
import java.util.TimerTask;

import ch.ethz.idsc.demo.mg.slam.SlamCoreContainer;
import ch.ethz.idsc.demo.mg.slam.SlamPrcContainer;
import ch.ethz.idsc.demo.mg.slam.config.SlamDvsConfig;
import ch.ethz.idsc.demo.mg.slam.core.PeriodicSlamStep;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.retina.util.math.Magnitude;

/** SLAM algorithm visualization wrapper. PeriodicSlamStep is implemented to have access to a time stamp for saving of frames */
public class SlamViewer extends PeriodicSlamStep implements StartAndStoppable {
  private final SlamPrcContainer slamPrcContainer;
  private final SlamMapFrame[] slamMapFrames;
  private final SlamMapGUI slamMapGUI;
  private final SlamSaveFrame slamSaveFrame;
  // ---
  private final Timer timer = new Timer();
  private final TimerTask visualizationTask = new TimerTask() {
    @Override
    public void run() {
      visualizationTask();
    }
  };
  private final long visualizationInterval;
  protected GokartPoseEvent gokartPoseEvent = GokartPoseEvents.motionlessUninitialized();
  protected final GokartPoseListener gokartPoseListener = getEvent -> gokartPoseEvent = getEvent;

  public SlamViewer(SlamCoreContainer slamCoreContainer, SlamPrcContainer slamPrcContainer) {
    super(slamCoreContainer, SlamDvsConfig.eventCamera.slamCoreConfig.savingInterval);
    this.slamPrcContainer = slamPrcContainer;
    slamMapGUI = new SlamMapGUI();
    slamMapFrames = new SlamMapFrame[2];
    for (int i = 0; i < slamMapFrames.length; ++i)
      slamMapFrames[i] = new SlamMapFrame();
    slamSaveFrame = new SlamSaveFrame(slamMapFrames);
    // ---
    visualizationInterval = Magnitude.MILLI_SECOND.toLong(SlamDvsConfig.eventCamera.slamCoreConfig.visualizationInterval);
  }

  @Override // from StartAndStoppable
  public void start() {
    timer.schedule(visualizationTask, 0, visualizationInterval);
  }

  @Override // from StartAndStoppable
  public void stop() {
    timer.cancel();
    slamMapGUI.dispose();
  }

  private void visualizationTask() {
    slamMapGUI.setFrames(StaticHelper.constructFrames(slamMapFrames, slamCoreContainer, slamPrcContainer, gokartPoseEvent.getPose()));
  }

  @Override // from PeriodicSlamStep
  protected void periodicTask(int currentTimeStamp, int lastComputationTimeStamp) {
    slamSaveFrame.saveFrame(currentTimeStamp);
  }
}
