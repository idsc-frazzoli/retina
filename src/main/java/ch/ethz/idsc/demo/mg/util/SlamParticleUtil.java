// code by mg
package ch.ethz.idsc.demo.mg.util;

import java.util.Arrays;
import java.util.Comparator;

import ch.ethz.idsc.demo.mg.slam.MapProvider;
import ch.ethz.idsc.demo.mg.slam.SlamParticle;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

// collection of public static methods to handle the mighty SlamParticle object
public enum SlamParticleUtil {
  ;
  public static final Comparator<SlamParticle> SlamCompare = new Comparator<SlamParticle>() {
    @Override
    public int compare(SlamParticle o1, SlamParticle o2) {
      if (o1.getParticleLikelihood() < o2.getParticleLikelihood())
        return 1;
      if (o1.getParticleLikelihood() > o2.getParticleLikelihood())
        return -1;
      return 0;
    }
  };

  /** initial distribution of slamParticles with a given pose and Gaussian distributed linear and angular velocities
   * 
   * @param slamParticles
   * @param pose initial pose which is identical for all particles
   * @param linVelAvg [m/s] average initial linear velocity
   * @param linVelStd [m/s] standard deviation of linear velocity
   * @param angVelStd [rad/s] standard deviation of angular velocity. initial angular velocity is set to 0 */
  public static void setInitialDistribution(SlamParticle[] slamParticles, Tensor pose, double linVelAvg, double linVelStd, double angVelStd) {
    double initLikelihood = 1.0 / slamParticles.length;
    for (int i = 0; i < slamParticles.length; i++) {
      double vx = SlamRandomUtil.getGaussian(linVelAvg, linVelStd);
      if (vx < 0)
        vx = 0;
      double vangle = SlamRandomUtil.getGaussian(0, angVelStd);
      slamParticles[i].initialize(pose, RealScalar.of(vx), RealScalar.of(vangle), initLikelihood);
    }
  }

  /** updates particle likelihoods by referring to a map
   * 
   * @param slamParticles
   * @param map
   * @param gokartFramePos [m] event position in go kart frame
   * @param alpha [-] update equation parameter */
  public static void updateLikelihoods(SlamParticle[] slamParticles, MapProvider map, double[] gokartFramePos, double alpha) {
    double sumOfLikelihoods = 0;
    for (int i = 0; i < slamParticles.length; i++) {
      Tensor worldCoord = slamParticles[i].getGeometricLayer().toVector(gokartFramePos[0], gokartFramePos[1]);
      double updatedParticleLikelihood = slamParticles[i].getParticleLikelihood() + alpha * map.getValue(worldCoord) / map.getMaxValue();
      slamParticles[i].setParticleLikelihood(updatedParticleLikelihood);
      sumOfLikelihoods += updatedParticleLikelihood;
    }
    // normalize particle likelihoods
    for (int i = 0; i < slamParticles.length; i++)
      slamParticles[i].setParticleLikelihood(slamParticles[i].getParticleLikelihood() / sumOfLikelihoods);
  }

  /** propagate the particles' state estimates with their estimated velocity
   * 
   * @param slamParticles
   * @param dT [s] */
  public static void propagateStateEstimate(SlamParticle[] slamParticles, double dT) {
    for (int i = 0; i < slamParticles.length; i++)
      slamParticles[i].propagateStateEstimate(dT);
  }

  /** propagate the particles' state estimates with the velocity provided by odometry
   * 
   * @param slamParticles
   * @param velocity provided by odometry
   * @param dT [s] */
  public static void propagateStateEstimateOdometry(SlamParticle[] slamParticles, Tensor velocity, double dT) {
    for (int i = 0; i < slamParticles.length; i++)
      slamParticles[i].propagateStateEstimateOdometry(velocity, dT);
  }

  /** particle resampling. multinominal sampling and neglect_low_likelihood method are available. particle roughening is also provided
   * 
   * @param slamParticles
   * @param dT [s]
   * @param rougheningLinVelStd [m/s] particle roughening parameter
   * @param rougheningAngVelStd [rad/s] particle roughening parameter */
  public static void resampleParticles(SlamParticle[] slamParticles, double dT, double rougheningLinVelStd, double rougheningAngVelStd) {
    // SlamParticleUtil.multinomialSampling(slamParticles);
    SlamParticleUtil.neglectLowLikelihoodds(slamParticles);
    // depending on resampling method, roughening might be used
    SlamParticleUtil.particleRoughening(slamParticles, dT, rougheningLinVelStd, rougheningAngVelStd);
  }

  private static void multinomialSampling(SlamParticle[] slamParticles) {
    int numbOfPart = slamParticles.length;
    // assigned particle numbers start at zero
    int[] assignedPart = new int[slamParticles.length];
    // generate array with cumulative particle probabilities
    double[] particleCDF = new double[numbOfPart];
    for (int i = 1; i < numbOfPart; i++) {
      particleCDF[i] = particleCDF[i - 1] + slamParticles[i].getParticleLikelihood();
    }
    // draw as many random numbers as particles and find corresponding CDF number
    double[] randomNumbers = new double[numbOfPart];
    SlamRandomUtil.setUniformRVArray(randomNumbers);
    for (int i = 0; i < numbOfPart; i++) {
      for (int j = 1; j < numbOfPart; j++) {
        if (randomNumbers[i] <= particleCDF[j]) {
          assignedPart[i] = j - 1;
          break;
        }
        assignedPart[i] = numbOfPart - 1;
      }
    }
    // set state once assignedPart array is determined
    double initLikelihood = (double) 1 / slamParticles.length;
    for (int i = 0; i < slamParticles.length; i++) {
      slamParticles[i].setStateFromParticle(slamParticles[assignedPart[i]], initLikelihood);
    }
  }

  // just throw away lowest likelihoods
  private static void neglectLowLikelihoodds(SlamParticle[] slamParticles) {
    double initLikelihood = (double) 1 / slamParticles.length;
    int neglectNumber = slamParticles.length / 2;
    // sort by likelihood
    Arrays.sort(slamParticles, SlamCompare);
    int startIndex = slamParticles.length - neglectNumber;
    for (int i = startIndex; i < slamParticles.length; i++) {
      slamParticles[i].setStateFromParticle(slamParticles[i - startIndex], initLikelihood);
    }
  }

  // roughens the linVel and angVel of the particles
  // TODO maybe roughen position as well? or only position
  private static void particleRoughening(SlamParticle[] slamParticles, double dT, double rougheningLinVelStd, double rougheningAngVelStd) {
    for (int i = 0; i < slamParticles.length; i++) {
      double vx = limitLinAccel(slamParticles[i].getLinVelDouble(), rougheningLinVelStd, dT);
      double vangle = limitAngAccel(slamParticles[i].getAngVelDouble(), slamParticles[i].getLinVelDouble(), rougheningAngVelStd, dT);
      slamParticles[i].setLinVel(RealScalar.of(vx));
      slamParticles[i].setAngVel(RealScalar.of(vangle));
    }
  }

  // incorporate the physical limits of linear acceleration and minVel/maxVel
  private static double limitLinAccel(double oldLinVel, double linVelRougheningStd, double dT) {
    double maxAccel = 2.5;
    double minAccel = -2.5;
    double minVel = 0;
    double maxVel = 8;
    double linAccel = SlamRandomUtil.getTrunctatedGaussian(0, linVelRougheningStd, minAccel, maxAccel);
    double newLinVel = oldLinVel + linAccel * dT;
    if (newLinVel < minVel)
      newLinVel = minVel;
    if (newLinVel > maxVel)
      newLinVel = maxVel;
    return newLinVel;
  }

  // incorporate the physical limits of angular acceleration and minVel/maxVel
  private static double limitAngAccel(double oldAngVel, double oldLinVel, double angVelRougheningStd, double dT) {
    double minAccel = -6;
    double maxAccel = 6;
    double turnRatePerMeter = 0.4082;
    double minVel = -turnRatePerMeter * oldLinVel;
    double maxVel = -minVel;
    double angAccel = SlamRandomUtil.getTrunctatedGaussian(0, angVelRougheningStd, minAccel, maxAccel);
    double newAngVel = oldAngVel + angAccel * dT;
    if (newAngVel < minVel)
      newAngVel = minVel;
    if (newAngVel > maxVel)
      newAngVel = maxVel;
    return newAngVel;
  }

  /** get average pose of particles in relevent range
   * 
   * @param slamParticles
   * @param relevantRange [-] number of particles with highest likelihood that is used
   * @return average pose
   */
  public static Tensor getAveragePose(SlamParticle[] slamParticles, int relevantRange) {
    Tensor expectedPose = Tensors.of(Quantity.of(0, SI.METER), Quantity.of(0, SI.METER), DoubleScalar.of(0));
    Arrays.sort(slamParticles, 0, relevantRange, SlamCompare);
    double likelihoodSum = 0;
    for (int i = 0; i < relevantRange; i++) {
      Tensor pose = slamParticles[i].getPose();
      double likelihood = slamParticles[i].getParticleLikelihood();
      likelihoodSum += likelihood;
      expectedPose = expectedPose.add(pose.multiply(RealScalar.of(likelihood)));
    }
    return expectedPose.divide(RealScalar.of(likelihoodSum));
  }

  public static void printStatusInfo(SlamParticle[] slamParticles) {
    Arrays.sort(slamParticles, SlamParticleUtil.SlamCompare);
    System.out.println("**** new status info **********");
    for (int i = 0; i < slamParticles.length; i++) {
      System.out.println("Particle likelihood " + slamParticles[i].getParticleLikelihood());
    }
  }
}
