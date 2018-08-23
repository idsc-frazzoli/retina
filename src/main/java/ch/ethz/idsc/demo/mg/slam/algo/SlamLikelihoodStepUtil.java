// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import ch.ethz.idsc.demo.mg.slam.MapProvider;
import ch.ethz.idsc.demo.mg.slam.SlamParticle;
import ch.ethz.idsc.tensor.Tensor;

/* package */ enum SlamLikelihoodStepUtil {
  ;
  /** updates particle likelihoods by referring to a map
   * 
   * @param slamParticles
   * @param map
   * @param gokartFramePos [m] event position in go kart frame
   * @param alpha [-] update equation parameter */
  public static void updateLikelihoods(SlamParticle[] slamParticles, MapProvider map, double[] gokartFramePos, double alpha) {
    double sumOfLikelihoods = 0;
    double maxValue = map.getMaxValue();
    for (int index = 0; index < slamParticles.length; ++index) {
      Tensor worldCoord = slamParticles[index].getGeometricLayer().toVector(gokartFramePos[0], gokartFramePos[1]);
      double updatedParticleLikelihood = //
          slamParticles[index].getParticleLikelihood() + alpha * map.getValue(worldCoord) / maxValue;
      slamParticles[index].setParticleLikelihood(updatedParticleLikelihood);
      sumOfLikelihoods += updatedParticleLikelihood;
    }
    // normalize particle likelihoods to sum up to 1
    for (int index = 0; index < slamParticles.length; ++index)
      slamParticles[index].setParticleLikelihood(slamParticles[index].getParticleLikelihood() / sumOfLikelihoods);
  }
}
