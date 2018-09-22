// code by mg
package ch.ethz.idsc.demo.mg.slam.core;

import ch.ethz.idsc.demo.mg.slam.MapProvider;
import ch.ethz.idsc.demo.mg.slam.SlamParticle;
import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum SlamLikelihoodStepUtil {
  ;
  /** updates particle likelihoods by referring to a map
   * 
   * @param slamParticles
   * @param map
   * @param gokartFramePos interpreted as [m] event position in go kart frame
   * @param alpha [-] update equation parameter */
  public static void updateLikelihoods(SlamParticle[] slamParticles, MapProvider map, double[] gokartFramePos, double alpha) {
    double sumOfLikelihoods = 0;
    double maxValue = map.getMaxValue();
    for (int i = 0; i < slamParticles.length; ++i) {
      Tensor worldCoord = new Se2Bijection(slamParticles[i].getPoseUnitless()).forward() //
          .apply(Tensors.vectorDouble(gokartFramePos));
      double updatedParticleLikelihood = //
          slamParticles[i].getParticleLikelihood() + alpha * map.getValue(worldCoord) / maxValue;
      slamParticles[i].setParticleLikelihood(updatedParticleLikelihood);
      sumOfLikelihoods += updatedParticleLikelihood;
    }
    // normalize particle likelihoods to sum up to 1
    for (int i = 0; i < slamParticles.length; ++i)
      slamParticles[i].setParticleLikelihood(slamParticles[i].getParticleLikelihood() / sumOfLikelihoods);
  }
}
