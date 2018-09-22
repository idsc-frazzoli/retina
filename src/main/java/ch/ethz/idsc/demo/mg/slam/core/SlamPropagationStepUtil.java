// code by mg
package ch.ethz.idsc.demo.mg.slam.core;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.ethz.idsc.demo.mg.slam.SlamParticle;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;

/** methods for state propagation of the SLAM algorithm */
/* package */ enum SlamPropagationStepUtil {
  ;
  /** propagate the particles' state estimates with their estimated velocity
   * 
   * @param slamParticles
   * @param dT interpreted as [s] */
  public static void propagateStateEstimate(SlamParticle[] slamParticles, double dT) {
    for (int i = 0; i < slamParticles.length; ++i)
      slamParticles[i].propagateStateEstimate(dT);
  }

  /** get average pose of the particles with highest likelihood
   * 
   * @param slamParticles
   * @param particleRange [-] >0 number of particles with highest likelihood that is employed
   * @return averagePose unitless representation */
  public static Tensor getAveragePose(SlamParticle[] slamParticles, int particleRange) {
    Stream.of(slamParticles) //
        .parallel() //
        .sorted(SlamParticleLikelihoodComparator.INSTANCE) //
        .limit(particleRange) //
        .collect(Collectors.toList());
    double likelihoodSum = 0;
    Tensor expectedPose = Array.zeros(3);
    for (int i = 0; i < particleRange; ++i) {
      double likelihood = slamParticles[i].getParticleLikelihood();
      likelihoodSum += likelihood;
      Tensor pose = slamParticles[i].getPoseUnitless();
      expectedPose = expectedPose.add(pose.multiply(RealScalar.of(likelihood)));
    }
    // likelihoods always sum up to 1 --> sum of highest likelihoods will never be zero
    return expectedPose.divide(RealScalar.of(likelihoodSum));
  }
}
