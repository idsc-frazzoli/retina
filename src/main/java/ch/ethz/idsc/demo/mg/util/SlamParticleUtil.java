// code by mg
package ch.ethz.idsc.demo.mg.util;

import ch.ethz.idsc.demo.mg.slam.MapProvider;
import ch.ethz.idsc.demo.mg.slam.SlamParticle;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

// collection of public static void methods to handle SlamParticle
// TODO change to interface with static implementations?
public class SlamParticleUtil {
  /** initial distribution with a given initial pose
   * 
   * @param pose
   * @param slamParticles */
  public static void setInitialDistribution(SlamParticle[] slamParticles, Tensor pose) {
    // TODO use deterministic distribution depending on particleNumber
    double initLikelihood = (double) 1 / slamParticles.length;
    for (int i = 0; i < slamParticles.length; i++) {
      slamParticles[i].initialize(pose, initLikelihood);
    }
  }

  public static void updateLikelihoods(SlamParticle[] slamParticles, MapProvider map, double[] gokartFramePos, double alpha ) {
    double sumOfLikelihoods = 0;
    for (int i = 0; i < slamParticles.length; i++) {
      // map go kart coordinates into world coordinates using the state estimate of the particle
      Tensor worldCoord = slamParticles[i].getGeometricLayer().toVector(gokartFramePos[0], gokartFramePos[1]);
      // get the likelihoodMap value of the computed world coordinate position and apply the actual update rule
      double updatedParticleLikelihood = slamParticles[i].getParticleLikelihood() + alpha * map.getValue(worldCoord);
      slamParticles[i].setParticleLikelihood(updatedParticleLikelihood);
      sumOfLikelihoods += updatedParticleLikelihood;
    }
    // normalize particle likelihoods
    for (int i = 0; i < slamParticles.length; i++)
      slamParticles[i].setParticleLikelihood(slamParticles[i].getParticleLikelihood() / sumOfLikelihoods);
  }

  /** propagate the particles' state estimates with corresponding parameters
   * 
   * @param linVelAvg
   * @param linVelStd
   * @param angVelStd
   * @param dT unit [s]
   * @param slamParticles SlamParticle array whose states will be propagated */
  public static void propagateStateEstimate(SlamParticle[] slamParticles, double linVelAvg, double linVelStd, double angVelStd, Scalar dT) {
    for (int i = 0; i < slamParticles.length; i++) {
      double vx = SlamRandomUtil.getGaussian(linVelAvg, linVelStd);
      double vangle = SlamRandomUtil.getGaussian(0, angVelStd);
      Tensor deltaPose = Tensors.vector(vx, 0, vangle);
      // use below for testing of accuracy of Se2Integrator
      // Tensor deltaPose = Tensors.vector(3, 0, 0);
      deltaPose = deltaPose.multiply(dT);
      slamParticles[i].propagateStateEstimate(deltaPose);
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
    SlamRandomUtil.setUniformRVArray(randomNumbers);
    // assigned particle numbers start at zero
    int[] assignedPart = new int[numbOfPart];
    for (int i = 0; i < numbOfPart; i++) {
      // System.out.println(randomNumbers[i]);
      for (int j = 1; j < numbOfPart; j++) {
        if (randomNumbers[i] <= particleCDF[j]) {
          assignedPart[i] = j - 1;
          break;
        }
        assignedPart[i] = numbOfPart - 1;
      }
    }
    Tensor[] extractedPoses = new Tensor[numbOfPart];
    SlamParticleUtil.extractPoses(slamParticles, extractedPoses);
    // set pose according to assignedParticles and normalize likelihoods
    double initLikelihood = (double) 1 / numbOfPart;
    for (int i = 0; i < numbOfPart; i++) {
      slamParticles[i].setPose(slamParticles[assignedPart[i]].getPose());
      slamParticles[i].setParticleLikelihood(initLikelihood);
    }
    SlamParticleUtil.particleRoughening(slamParticles);
  }

  private static void extractPoses(SlamParticle[] slamParticles, Tensor[] extractedPoses) {
    for (int i = 0; i < slamParticles.length; i++)
      extractedPoses[i] = slamParticles[i].getPose();
  }

  private static void particleRoughening(SlamParticle[] slamParticles) {
    // disturb the pose of the particles
  }

  // get average pose
  public static Tensor getAveragePose(SlamParticle[] slamParticles) {
    Tensor expectedPose = Tensors.of(Quantity.of(0, SI.METER), Quantity.of(0, SI.METER), DoubleScalar.of(0));
    for (int i = 0; i < slamParticles.length; i++) {
      Tensor pose = slamParticles[i].getPose();
      double likelihood = slamParticles[i].getParticleLikelihood();
      expectedPose = expectedPose.add(pose.multiply(RealScalar.of(likelihood)));
    }
    return expectedPose;
  }

  // get maximum likelihood pose
  public static Tensor getMLPose(SlamParticle[] slamParticles) {
    double maxLikelihood = 0;
    int maxLikelihoodIndex = 0;
    for (int i = 0; i < slamParticles.length; i++) {
      if (slamParticles[i].getParticleLikelihood() > maxLikelihood) {
        maxLikelihood = slamParticles[i].getParticleLikelihood();
        maxLikelihoodIndex = i;
      }
    }
    return slamParticles[maxLikelihoodIndex].getPose();
  }
}
