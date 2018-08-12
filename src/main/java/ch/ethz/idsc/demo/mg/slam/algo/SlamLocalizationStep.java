// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import ch.ethz.idsc.demo.mg.slam.MapProvider;
import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamParticle;
import ch.ethz.idsc.demo.mg.util.slam.SlamParticleUtil;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Tensor;

/** executes the localization step of the SLAM algorithm */
/* package */ class SlamLocalizationStep {
  private final SlamEstimatedPose slamEstimatedPose;
  private final boolean odometryStatePropagation;
  private final double resampleRate;
  private final double statePropagationRate;
  private final double rougheningLinVelStd;
  private final double rougheningAngVelStd;
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
    statePropagationRate = Magnitude.SECOND.toDouble(slamConfig._statePropagationRate);
    rougheningLinVelStd = slamConfig.rougheningLinAccelStd.number().doubleValue();
    rougheningAngVelStd = slamConfig.rougheningAngAccelStd.number().doubleValue();
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

  public void localizationStep(SlamParticle[] slamParticles, MapProvider map, Tensor odometryVel, double[] eventGokartFrame, double currentTimeStamp) {
    if (currentTimeStamp - lastPropagationTimeStamp > statePropagationRate) {
      double dT = currentTimeStamp - lastPropagationTimeStamp;
      if (odometryStatePropagation)
        SlamParticleUtil.propagateStateEstimateOdometry(slamParticles, odometryVel, dT);
      else
        SlamParticleUtil.propagateStateEstimate(slamParticles, dT);
      lastPropagationTimeStamp = currentTimeStamp;
    }
    if (currentTimeStamp - lastResampleTimeStamp > resampleRate) {
      double dT = currentTimeStamp - lastResampleTimeStamp;
      SlamParticleUtil.resampleParticles(slamParticles, dT, rougheningLinVelStd, rougheningAngVelStd);
      lastResampleTimeStamp = currentTimeStamp;
    }
    if (eventGokartFrame[0] < lookAheadDistance)
      SlamParticleUtil.updateLikelihoods(slamParticles, map, eventGokartFrame, alpha);
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
