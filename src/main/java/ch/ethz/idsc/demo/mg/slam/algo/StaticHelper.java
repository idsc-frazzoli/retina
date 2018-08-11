// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.Arrays;

import ch.ethz.idsc.demo.mg.slam.SlamParticle;
import ch.ethz.idsc.demo.mg.util.slam.SlamParticleLikelihoodComparator;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;

enum StaticHelper {
  ;
  /** Careful: ordering of given array slamParticles is subject to change
   * 
   * get average pose of particles in relevant range
   * 
   * @param slamParticles
   * @param relevantRange [-] number of particles with highest likelihood that is used
   * @return averagePose unitless representation */
  public static Tensor getAveragePose(SlamParticle[] slamParticles, int relevantRange) {
    // TODO MG the sorting does not move the most relevant to the front! instead would have to sort over entire list?
    // ... comment on what relevantRange is intended to represent
    Arrays.parallelSort(slamParticles, 0, relevantRange, SlamParticleLikelihoodComparator.INSTANCE);
    double likelihoodSum = 0;
    Tensor expectedPose = Array.zeros(3);
    for (int i = 0; i < relevantRange; ++i) {
      double likelihood = slamParticles[i].getParticleLikelihood();
      likelihoodSum += likelihood;
      Tensor pose = slamParticles[i].getPoseUnitless();
      expectedPose = expectedPose.add(pose.multiply(RealScalar.of(likelihood)));
    }
    // TODO MG comment on case likelihoodSum == 0, why not possible?
    return expectedPose.divide(RealScalar.of(likelihoodSum));
  }
}
