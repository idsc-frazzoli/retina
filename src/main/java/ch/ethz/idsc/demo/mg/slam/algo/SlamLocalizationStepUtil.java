// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.ethz.idsc.demo.mg.slam.MapProvider;
import ch.ethz.idsc.demo.mg.slam.SlamParticle;
import ch.ethz.idsc.demo.mg.util.slam.SlamParticleLikelihoodComparator;
import ch.ethz.idsc.demo.mg.util.slam.SlamRandomUtil;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.qty.Quantity;

/** collection of methods for the localization step of the SLAM algorithm */
public enum SlamLocalizationStepUtil {
  ;
  /** max turning rate per meter
   * 
   * https://github.com/idsc-frazzoli/retina/files/1958724/20180429_minimum_turning_radius.pdf */
  public static final Scalar TURNING_RATIO_MAX = Quantity.of(0.4082, "rad*m^-1");

  /** updates particle likelihoods by referring to a map
   * 
   * @param slamParticles
   * @param map
   * @param gokartFramePos [m] event position in go kart frame
   * @param alpha [-] update equation parameter */
  public static void updateLikelihoods(SlamParticle[] slamParticles, MapProvider map, double[] gokartFramePos, double alpha) {
    double sumOfLikelihoods = 0;
    double maxValue = map.getMaxValue();
    for (int i = 0; i < slamParticles.length; i++) {
      Tensor worldCoord = slamParticles[i].getGeometricLayer().toVector(gokartFramePos[0], gokartFramePos[1]);
      double updatedParticleLikelihood = slamParticles[i].getParticleLikelihood() + alpha * map.getValue(worldCoord) / maxValue;
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
   * @param velocity {[m/s],[m/s],[-]} provided by odometry
   * @param dT interpreted as [s] */
  public static void propagateStateEstimateOdometry(SlamParticle[] slamParticles, Tensor velocity, double dT) {
    for (int i = 0; i < slamParticles.length; i++)
      slamParticles[i].propagateStateEstimateOdometry(velocity, dT);
  }

  /** particle resampling. multinominal sampling and neglect_low_likelihood method are available. particle roughening is also provided
   * 
   * @param slamParticles
   * @param dT interpreted as [s]
   * @param rougheningLinVelStd [m/s] particle roughening parameter
   * @param rougheningAngVelStd [rad/s] particle roughening parameter */
  public static void resampleParticles(SlamParticle[] slamParticles, double dT, double rougheningLinVelStd, double rougheningAngVelStd) {
    // SlamParticleUtil.multinomialSampling(slamParticles);
    SlamLocalizationStepUtil.neglectLowLikelihoods(slamParticles);
    // depending on resampling method, roughening might be used
    SlamLocalizationStepUtil.particleRoughening(slamParticles, dT, rougheningLinVelStd, rougheningAngVelStd);
  }

  /** initial distribution of slamParticles with a given pose and Gaussian distributed linear and angular velocities
   * 
   * @param slamParticles
   * @param pose {[m],[m],[-]} initial pose which is identical for all particles
   * @param linVelAvg [m/s] average initial linear velocity
   * @param linVelStd [m/s] standard deviation of linear velocity
   * @param angVelStd [rad/s] standard deviation of angular velocity. initial angular velocity is set to 0 */
  public static void setInitialDistribution(SlamParticle[] slamParticles, Tensor pose, double linVelAvg, double linVelStd, double angVelStd) {
    double initLikelihood = 1.0 / slamParticles.length;
    for (int i = 0; i < slamParticles.length; i++) {
      // TODO magic constants 0 and 8. 8 is maximum velocity [m*s^-1]
      double linVel = SlamRandomUtil.getTruncatedGaussian(linVelAvg, linVelStd, 0, 8);
      double turnRatePerMeter = Magnitude.PER_METER.toDouble(TURNING_RATIO_MAX);
      double maxAngVel = turnRatePerMeter * linVel;
      double minAngVel = -maxAngVel;
      double angVel = SlamRandomUtil.getTruncatedGaussian(0, angVelStd, minAngVel, maxAngVel);
      slamParticles[i].initialize(pose, RealScalar.of(linVel), RealScalar.of(angVel), initLikelihood);
    }
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

  /** standard multinominal resampling method
   * 
   * @param slamParticles */
  private static void multinomialResampling(SlamParticle[] slamParticles) {
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

  /** non-standard resampling method
   * 
   * @param slamParticles */
  private static void neglectLowLikelihoods(SlamParticle[] slamParticles) {
    double initLikelihood = (double) 1 / slamParticles.length;
    // sort by likelihood
    Arrays.parallelSort(slamParticles, SlamParticleLikelihoodComparator.INSTANCE);
    int startIndex = slamParticles.length / 2;
    // duplicate half of particles with highest likelihood
    for (int i = startIndex; i < slamParticles.length; i++) {
      slamParticles[i].setStateFromParticle(slamParticles[i - startIndex], initLikelihood);
    }
    // reset likelihoods of remaining particles
    for (int i = 0; i < startIndex; i++) {
      slamParticles[i].setParticleLikelihood(initLikelihood);
    }
  }

  /** disturbs the particle states with trunctuated Gaussian noise
   * 
   * @param slamParticles
   * @param dT interpreted as [s]
   * @param rougheningLinVelStd interpreted as [m/s]
   * @param rougheningAngVelStd interpreted as [rad/s] */
  private static void particleRoughening(SlamParticle[] slamParticles, double dT, double rougheningLinVelStd, double rougheningAngVelStd) {
    for (int i = 0; i < slamParticles.length; i++) {
      double linVel = limitLinAccel(slamParticles[i].getLinVelDouble(), rougheningLinVelStd, dT);
      double angVel = limitAngAccel(slamParticles[i].getAngVelDouble(), slamParticles[i].getLinVelDouble(), rougheningAngVelStd, dT);
      slamParticles[i].setLinVel(RealScalar.of(linVel));
      slamParticles[i].setAngVel(RealScalar.of(angVel));
    }
  }

  /** disturbs the linVel state with Gaussian noise while not violating the linear acceleration limits of the vehicle
   * 
   * @param oldLinVel current linVel state
   * @param linVelRougheningStd interpreted as [m/s] standard deviation of additive Gaussian noise
   * @param dT interpreted as [s]
   * @return updated disturbed linVel */
  // TODO MG hard-coded acceleration and velocity limits
  private static double limitLinAccel(double oldLinVel, double linVelRougheningStd, double dT) {
    double maxAccel = 2.5;
    double minAccel = -2.5;
    double minVel = 0;
    double maxVel = 8;
    double linAccel = SlamRandomUtil.getTruncatedGaussian(0, linVelRougheningStd, minAccel, maxAccel);
    double newLinVel = oldLinVel + linAccel * dT;
    if (newLinVel < minVel) {
      newLinVel = minVel;
      return newLinVel;
    }
    if (newLinVel > maxVel) {
      newLinVel = maxVel;
      return newLinVel;
    }
    return newLinVel;
  }

  /** disturbs the angVel state with Gaussian noise while not violating the angular acceleration limits of the vehicle
   * 
   * @param oldAngVel current angVel state
   * @param oldLinVel current linVel state
   * @param angVelRougheningStd interpreted as [rad/s] standard deviation of additive Gaussian noise
   * @param dT interpreted as [s]
   * @return updated disturbed angVel */
  // TODO MG more hard-coded acceleration and velocity limits
  private static double limitAngAccel(double oldAngVel, double oldLinVel, double angVelRougheningStd, double dT) {
    double minAccel = -6;
    double maxAccel = 6;
    double minVel = -TURNING_RATIO_MAX.number().doubleValue() * oldLinVel;
    double maxVel = -minVel;
    double angAccel = SlamRandomUtil.getTruncatedGaussian(0, angVelRougheningStd, minAccel, maxAccel);
    double newAngVel = oldAngVel + angAccel * dT;
    if (newAngVel < minVel) {
      newAngVel = minVel;
      return newAngVel;
    }
    if (newAngVel > maxVel) {
      newAngVel = maxVel;
      return newAngVel;
    }
    return newAngVel;
  }

  public static void printStatusInfo(SlamParticle[] slamParticles) {
    Arrays.parallelSort(slamParticles, SlamParticleLikelihoodComparator.INSTANCE);
    System.out.println("**** new status info **********");
    for (int i = 0; i < slamParticles.length; i++) {
      System.out.println("Particle likelihood " + slamParticles[i].getParticleLikelihood());
    }
  }
}
