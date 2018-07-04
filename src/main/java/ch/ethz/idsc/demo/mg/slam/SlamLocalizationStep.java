// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;
import ch.ethz.idsc.demo.mg.util.SlamParticleUtil;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.tensor.Tensor;

// executes the localization step of the SLAM algorithm
public class SlamLocalizationStep {
  private final SlamEstimatedPose estimatedPose;
  private final double resampleRate = 0.01;;
  private final double propagationRate = 0.001;
  private final double linVelAvg;
  private final double linVelStd;
  private final double angVelStd;
  private double lastResampleTimeStamp;
  private double lastPropagationTimeStamp;

  SlamLocalizationStep(PipelineConfig pipelineConfig) {
    estimatedPose = new SlamEstimatedPose();
    linVelAvg = pipelineConfig.linVelAvg.number().doubleValue();
    linVelStd = pipelineConfig.linVelStd.number().doubleValue();
    angVelStd = pipelineConfig.angVelStd.number().doubleValue();
  }

  public void initialize(SlamParticle[] slamParticles, Tensor initPose, double initTimeStamp) {
    SlamParticleUtil.setInitialDistribution(slamParticles, initPose, linVelAvg, linVelStd, angVelStd);
    estimatedPose.setPose(initPose);
    lastResampleTimeStamp = initTimeStamp;
    lastPropagationTimeStamp = initTimeStamp;
  }

  public void localizationStep(SlamParticle[] slamParticles, double[] eventGokartFrame, double currentTimeStamp) {
    if ((currentTimeStamp - lastPropagationTimeStamp) > propagationRate) {
      SlamParticleUtil.propagateStateEstimate(slamParticles, currentTimeStamp - lastPropagationTimeStamp);
      lastPropagationTimeStamp = currentTimeStamp;
    }
    if ((currentTimeStamp - lastResampleTimeStamp) > resampleRate) {
      SlamParticleUtil.resampleParticles(slamParticles, currentTimeStamp - lastResampleTimeStamp);
      lastResampleTimeStamp = currentTimeStamp;
    }
    estimatedPose.setPose(SlamParticleUtil.getAveragePose(slamParticles, 1));
  }

  public GokartPoseInterface getPoseInterface() {
    return estimatedPose;
  }
}
