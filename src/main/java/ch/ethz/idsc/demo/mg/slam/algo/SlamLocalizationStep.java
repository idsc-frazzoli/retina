// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import ch.ethz.idsc.demo.mg.slam.MapProvider;
import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamParticle;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Tensor;

/** executes the localization step of the SLAM algorithm */
/* package */ class SlamLocalizationStep {
  private final SlamEstimatedPose slamEstimatedPose;
  private final boolean odometryStatePropagation;
  private final boolean onlineMode;
  private final double resampleRate;
  private final double statePropagationRate;
  private final double rougheningLinAccelStd;
  private final double rougheningAngAccelStd;
  private final double linVelAvg;
  private final double linVelStd;
  private final double angVelStd;
  private final double lookAheadDistance;
  private final double alpha;
  private double lastResampleTimeStamp;
  private double lastPropagationTimeStamp;

  SlamLocalizationStep(SlamConfig slamConfig) {
    slamEstimatedPose = new SlamEstimatedPose();
    resampleRate = Magnitude.SECOND.toDouble(slamConfig._resampleRate);
    odometryStatePropagation = slamConfig.odometryStatePropagation;
    onlineMode = slamConfig.onlineMode;
    statePropagationRate = Magnitude.SECOND.toDouble(slamConfig._statePropagationRate);
    rougheningLinAccelStd = Magnitude.ACCELERATION.toDouble(slamConfig._rougheningLinAccelStd);
    rougheningAngAccelStd = Magnitude.ANGULAR_ACCELERATION.toDouble(slamConfig._rougheningAngAccelStd);
    linVelAvg = Magnitude.VELOCITY.toDouble(slamConfig._linVelAvg);
    linVelStd = Magnitude.VELOCITY.toDouble(slamConfig._linVelStd);
    angVelStd = Magnitude.ANGULAR_RATE.toDouble(slamConfig._angVelStd);
    lookAheadDistance = Magnitude.METER.toDouble(slamConfig._lookAheadDistance);
    alpha = slamConfig.alpha.number().doubleValue();
  }

  /** @param slamParticles
   * @param initPose {[m],[m],[-]} provided by lidar
   * @param initTimeStamp [s] */
  public void initialize(SlamParticle[] slamParticles, Tensor initPose, double initTimeStamp) {
    SlamLocalizationStepUtil.setInitialDistribution(slamParticles, initPose, linVelAvg, linVelStd, angVelStd);
    slamEstimatedPose.setPose(initPose);
    lastResampleTimeStamp = initTimeStamp;
    lastPropagationTimeStamp = initTimeStamp;
  }

  /** propagates the state of the particles and updates their likelihoods
   * 
   * @param slamParticles
   * @param map occurrence map used for localization
   * @param odometryVel {[m/s],[m/s],[-]} provided by odometry
   * @param eventGokartFrame event position in gokart frame interpreted as [m]
   * @param currentTimeStamp interpreted as [s] */
  public void localizationStep(SlamParticle[] slamParticles, MapProvider map, Tensor odometryVel, double[] eventGokartFrame, double currentTimeStamp) {
    if (eventGokartFrame[0] < lookAheadDistance)
      SlamLocalizationStepUtil.updateLikelihoods(slamParticles, map, eventGokartFrame, alpha);
    // ---
    if (!onlineMode && (currentTimeStamp - lastPropagationTimeStamp > statePropagationRate)) {
      double dT = currentTimeStamp - lastPropagationTimeStamp;
      if (odometryStatePropagation)
        SlamLocalizationStepUtil.propagateStateEstimateOdometry(slamParticles, odometryVel, dT);
      else
        SlamLocalizationStepUtil.propagateStateEstimate(slamParticles, dT);
      lastPropagationTimeStamp = currentTimeStamp;
    }
    if (currentTimeStamp - lastResampleTimeStamp > resampleRate) {
      double dT = currentTimeStamp - lastResampleTimeStamp;
      SlamLocalizationStepUtil.resampleParticles(slamParticles, dT, rougheningLinAccelStd, rougheningAngAccelStd);
      lastResampleTimeStamp = currentTimeStamp;
    }
    // ---
    slamEstimatedPose.setPoseUnitless(SlamLocalizationStepUtil.getAveragePose(slamParticles, 1));
  }

  /** used to set pose using lidar ground truth
   * 
   * @param pose {[m],[m],[-]} provided by lidar */
  public void setPose(Tensor pose) {
    slamEstimatedPose.setPose(pose);
  }

  public SlamEstimatedPose getSlamEstimatedPose() {
    return slamEstimatedPose;
  }
}
