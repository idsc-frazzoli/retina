// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import ch.ethz.idsc.demo.mg.slam.SlamParticle;

/** standard multinominal resampling method for the SLAM algorithm */
/* package */ enum MultinomialResampling {
  ;
  /** @param slamParticles */
  static void multinomialResampling(SlamParticle[] slamParticles) {
    int numbOfPart = slamParticles.length;
    // assigned particle numbers start at zero
    int[] assignedPart = new int[slamParticles.length];
    // generate array with cumulative particle probabilities
    double[] particleCDF = new double[numbOfPart];
    for (int i = 1; i < numbOfPart; i++)
      particleCDF[i] = particleCDF[i - 1] + slamParticles[i].getParticleLikelihood();
    // draw as many random numbers as particles and find corresponding CDF number
    double[] randomNumbers = new double[numbOfPart];
    StaticHelper.setUniformRVArray(randomNumbers);
    for (int i = 0; i < numbOfPart; i++)
      for (int j = 1; j < numbOfPart; j++) {
        if (randomNumbers[i] <= particleCDF[j]) {
          assignedPart[i] = j - 1;
          break;
        }
        assignedPart[i] = numbOfPart - 1;
      }
    // set state once assignedPart array is determined
    double initLikelihood = (double) 1 / slamParticles.length;
    for (int i = 0; i < slamParticles.length; i++)
      slamParticles[i].setStateFromParticle(slamParticles[assignedPart[i]], initLikelihood);
  }
}
