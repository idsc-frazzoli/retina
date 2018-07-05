// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.demo.mg.util.SlamParticleUtil;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.tensor.Tensor;

// executes the localization step of the SLAM algorithm
public class SlamLocalizationStep {
  private final SlamEstimatedPose estimatedPose;
  private final double resampleRate;
  private final double statePropagationRate;
  private final double rougheningLinVelStd;
  private final double rougheningAngVelStd;
  private final double linVelAvg;
  private final double linVelStd;
  private final double angVelStd;
  private double lastResampleTimeStamp;
  private double lastPropagationTimeStamp;

  SlamLocalizationStep(SlamConfig slamConfig) {
    estimatedPose = new SlamEstimatedPose();
    resampleRate = slamConfig.resampleRate.number().doubleValue();
    statePropagationRate = slamConfig.statePropagationRate.number().doubleValue();
    rougheningLinVelStd = slamConfig.rougheningLinVelStd.number().doubleValue();
    rougheningAngVelStd = slamConfig.rougheningAngVelStd.number().doubleValue();
    linVelAvg = slamConfig.linVelAvg.number().doubleValue();
    linVelStd = slamConfig.linVelStd.number().doubleValue();
    angVelStd = slamConfig.angVelStd.number().doubleValue();
  }

  public void initialize(SlamParticle[] slamParticles, Tensor initPose, double initTimeStamp) {
    SlamParticleUtil.setInitialDistribution(slamParticles, initPose, linVelAvg, linVelStd, angVelStd);
    estimatedPose.setPose(initPose);
    lastResampleTimeStamp = initTimeStamp;
    lastPropagationTimeStamp = initTimeStamp;
  }

  public void localizationStep(SlamParticle[] slamParticles, double[] eventGokartFrame, double currentTimeStamp) {
    if ((currentTimeStamp - lastPropagationTimeStamp) > statePropagationRate) {
      double dT1 = currentTimeStamp - lastPropagationTimeStamp;
      SlamParticleUtil.propagateStateEstimate(slamParticles, dT1);
      lastPropagationTimeStamp = currentTimeStamp;
    }
    if ((currentTimeStamp - lastResampleTimeStamp) > resampleRate) {
      double dT2 = currentTimeStamp - lastResampleTimeStamp;
      SlamParticleUtil.resampleParticles(slamParticles, dT2, rougheningLinVelStd, rougheningAngVelStd);
      lastResampleTimeStamp = currentTimeStamp;
    }
    estimatedPose.setPose(SlamParticleUtil.getAveragePose(slamParticles, 1));
  }

  public void setPose(Tensor pose) {
    estimatedPose.setPose(pose);
  }
  public GokartPoseInterface getPoseInterface() {
    return estimatedPose;
  }
}
