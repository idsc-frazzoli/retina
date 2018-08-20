// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.Timer;
import java.util.TimerTask;

import ch.ethz.idsc.demo.mg.slam.GokartPoseOdometryDemo;
import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamParticle;
import ch.ethz.idsc.retina.util.math.Magnitude;

/* collects the timedTasks for the SLAM algorithm */
// idea: collect all timedTasks in this class to not blow up SlamProvider class.
// as disadvantage, we need a lot of fields for all the timedTasks plus all main fields of the SLAM algorithm as well.
// did not find a way to implement the timerTasks in e.g. SlamLocalizationStep because 
// the arguments are only available once periodic method is called.
// TODO could maybe be refactored into enum or static class
public class SlamTimerTask {
  private final GokartPoseOdometryDemo gokartPoseOdometry;
  private final SlamParticle[] slamParticles;
  private final SlamMappingStep slamMappingStep;
  private final SlamLocalizationStep slamLocalizationStep;
  private final SlamMapProcessing slamMapProcessing;
  private final Timer timer;
  // ---
  private final long reactiveUpdateRate;
  private final boolean reactiveMappingMode;
  private final double lookBehindDistance;
  private final TimerTask reactiveOccurrenceMapTask;
  // ---
  private final long statePropagationRate;
  private final double dT;
  private final boolean odometryStatePropagation;
  private final TimerTask statePropagationTask;
  // ---
  private final long wayPointUpdateRate;
  private final TimerTask mapProcessingTask;

  public SlamTimerTask(Timer timer, SlamParticle[] slamParticles, GokartPoseOdometryDemo gokartPoseOdometry, //
      SlamConfig slamConfig, SlamMappingStep slamMappingStep, SlamLocalizationStep slamLocalizationStep, SlamMapProcessing slamMapProcessing) {
    this.slamParticles = slamParticles;
    this.slamMappingStep = slamMappingStep;
    this.slamLocalizationStep = slamLocalizationStep;
    this.gokartPoseOdometry = gokartPoseOdometry;
    this.slamMapProcessing = slamMapProcessing;
    this.timer = timer;
    // ---
    reactiveUpdateRate = Magnitude.MILLI_SECOND.toLong(slamConfig.reactiveUpdateRate);
    reactiveMappingMode = slamConfig.reactiveMappingMode;
    lookBehindDistance = Magnitude.METER.toDouble(slamConfig.lookBehindDistance);
    reactiveOccurrenceMapTask = new TimerTask() {
      @Override
      public void run() {
        reactiveOccurrenceMapTask();
      }
    };
    // ---
    statePropagationRate = Magnitude.MILLI_SECOND.toLong(slamConfig.statePropagationRate);
    dT = Magnitude.SECOND.toDouble(slamConfig.statePropagationRate);
    odometryStatePropagation = slamConfig.odometryStatePropagation;
    statePropagationTask = new TimerTask() {
      @Override
      public void run() {
        statePropagationTask();
      }
    };
    // ---
    wayPointUpdateRate = Magnitude.MILLI_SECOND.toLong(slamConfig.wayPointUpdateRate);
    mapProcessingTask = new TimerTask() {
      @Override
      public void run() {
        mapProcessingTask();
      }
    };
  }

  public void scheduleTasks() {
    timer.schedule(reactiveOccurrenceMapTask, 0, reactiveUpdateRate);
    timer.scheduleAtFixedRate(statePropagationTask, 0, statePropagationRate); // important that dT for state propagation is fixed
    timer.schedule(mapProcessingTask, 0, wayPointUpdateRate);
  }

  private void reactiveOccurrenceMapTask() {
    if (reactiveMappingMode)
      SlamMappingStepUtil.updateReactiveOccurrenceMap(slamLocalizationStep.getSlamEstimatedPose().getPoseUnitless(), //
          slamMappingStep.getOccurrenceMap(), lookBehindDistance);
  }

  private void statePropagationTask() {
    if (odometryStatePropagation)
      SlamLocalizationStepUtil.propagateStateEstimateOdometry(slamParticles, gokartPoseOdometry.getVelocity(), dT);
    else
      SlamLocalizationStepUtil.propagateStateEstimate(slamParticles, dT);
  }

  private void mapProcessingTask() {
    slamMapProcessing.mapPostProcessing(slamMappingStep.getOccurrenceMap());
  }
}
