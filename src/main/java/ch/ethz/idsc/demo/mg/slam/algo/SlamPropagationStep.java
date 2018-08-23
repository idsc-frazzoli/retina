// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.Objects;

import ch.ethz.idsc.demo.mg.slam.SlamConfig;
import ch.ethz.idsc.demo.mg.slam.SlamContainer;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;
import ch.ethz.idsc.retina.util.math.Magnitude;

/** particle's state propagation which should be followed by a resampling step */
/* package */ class SlamPropagationStep extends AbstractSlamStep {
  private final double statePropagationRate;
  // ---
  private Double lastPropagationTimeStamp = null;

  protected SlamPropagationStep(SlamConfig slamConfig, SlamContainer slamContainer) {
    super(slamContainer);
    statePropagationRate = Magnitude.SECOND.toDouble(slamConfig.statePropagationRate);
  }

  @Override // from DavisDvsListener
  public void davisDvs(DavisDvsEvent davisDvsEvent) {
    double currentTimeStamp = davisDvsEvent.time * 1E-6;
    initializeTimeStamps(currentTimeStamp);
    if (currentTimeStamp - lastPropagationTimeStamp > statePropagationRate) {
      propagateStateEstimate(currentTimeStamp, lastPropagationTimeStamp);
      setSlamEstimatedPose();
      lastPropagationTimeStamp = currentTimeStamp;
    }
  }

  private void initializeTimeStamps(double currentTimeStamp) {
    if (Objects.isNull(lastPropagationTimeStamp))
      lastPropagationTimeStamp = currentTimeStamp;
  }

  private void propagateStateEstimate(double currentTimeStamp, double lastPropagationTimeStamp) {
    double dT = currentTimeStamp - lastPropagationTimeStamp;
    SlamPropagationStepUtil.propagateStateEstimate(slamContainer.getSlamParticles(), dT);
  }

  private void setSlamEstimatedPose() {
    slamContainer.getSlamEstimatedPose().setPoseUnitless(SlamPropagationStepUtil.getAveragePose(slamContainer.getSlamParticles(), 1));
  }
}
