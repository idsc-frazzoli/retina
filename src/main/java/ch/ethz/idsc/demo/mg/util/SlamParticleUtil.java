// code by mg
package ch.ethz.idsc.demo.mg.util;

import ch.ethz.idsc.demo.mg.slam.SlamParticle;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

// collection of public static void methods to handle SlamParticle
public class SlamParticleUtil {
  /** propagate the particles' state estimates with corresponding parameters
   * 
   * @param linVelAvg
   * @param linVelStd
   * @param angVelStd
   * @param dT unit [s]
   * @param slamParticles SlamParticle array whose states will be propagated */
  public static void propagateStateEstimate(double linVelAvg, double linVelStd, double angVelStd, Scalar dT, SlamParticle[] slamParticles) {
    int numberOfParticles = slamParticles.length;
    for (int i = 0; i < numberOfParticles; i++) {
      double vx = SlamRandomUtil.getGaussian(linVelAvg, linVelStd);
      double vangle = SlamRandomUtil.getGaussian(0, angVelStd);
      Tensor deltaPose = Tensors.vector(vx, 0, vangle);
      // use below for testing of accuracy of Se2Integrator
      // Tensor deltaPose = Tensors.vector(3, 0, 0);
      deltaPose = deltaPose.multiply(dT);
      slamParticles[i].propagateStateEstimate(deltaPose);
    }
  }

  /** initial distribution with a given initial pose
   * 
   * @param pose
   * @param slamParticles */
  public static void setInitialDistribution(Tensor pose, SlamParticle[] slamParticles) {
    // TODO use deterministic distribution depending on particleNumber
    double initLikelihood = (double) 1 / slamParticles.length;
    for (int i = 0; i < slamParticles.length; i++) {
      slamParticles[i].initialize(pose, initLikelihood);
    }
  }

  /** resamples the particles
   * 
   * @param slamParticles */
  public static void resampleParticles(SlamParticle[] slamParticles) {
    // first approach: standard multinomial sampling
    int numbOfPart = slamParticles.length;
    // generate array with cumulative particle probabilities
    double[] particleCDF = new double[numbOfPart];
    for (int i = 1; i < numbOfPart; i++) {
      particleCDF[i] = particleCDF[i - 1] + slamParticles[i].getParticleLikelihood();
    }
    // draw as many random numbers as particles and find corresponding CDF number
    double[] randomNumbers = new double[numbOfPart];
    // assigned particle numbers start at zero
    int[] assignedPart = new int[numbOfPart];
    for (int i = 0; i < numbOfPart; i++) {
      randomNumbers[i] = SlamRandomUtil.getUniformRV();
      for (int j = 1; j < numbOfPart; j++) {
        if (randomNumbers[i] <= particleCDF[j]) {
          assignedPart[i] = j - 1;
          break;
        }
        assignedPart[i] = numbOfPart - 1;
      }
    }
    // extract all the poses
    Tensor[] extractedPoses = new Tensor[numbOfPart];
    for (int i = 0; i < numbOfPart; i++) {
      extractedPoses[i] = slamParticles[i].getPose();
    }
    // set pose according to assignedParticles and normalize likelihoods
    // TODO particle roughening
    double initLikelihood = (double) 1 / numbOfPart;
    for (int i = 0; i < numbOfPart; i++) {
      slamParticles[i].setPose(slamParticles[assignedPart[i]].getPose());
      slamParticles[i].setParticleLikelihood(initLikelihood);
    }
  }
}
