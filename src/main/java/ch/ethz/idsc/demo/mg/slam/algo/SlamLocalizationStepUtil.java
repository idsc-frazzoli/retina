// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import ch.ethz.idsc.demo.mg.slam.MapProvider;
import ch.ethz.idsc.demo.mg.slam.SlamParticle;
import ch.ethz.idsc.demo.mg.util.slam.SlamParticleLikelihoodComparator;
import ch.ethz.idsc.demo.mg.util.slam.SlamRandomUtil;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;

/** collection of methods for the localization step of the SLAM algorithm */
public enum SlamLocalizationStepUtil {
  ;
  private static final double TURN_RATE_PER_METER = //
      Magnitude.PER_METER.toDouble(SteerConfig.GLOBAL.turningRatioMax);
  private static final double LINVEL_MIN = 0; // "m/s"
  private static final double LINVEL_MAX = 8; // "m/s"

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

  /** propagate the particles' state estimates with their estimated velocity
   * 
   * @param slamParticles
   * @param dT [s] */
  public static void propagateStateEstimate(SlamParticle[] slamParticles, double dT) {
    for (int index = 0; index < slamParticles.length; ++index)
      slamParticles[index].propagateStateEstimate(dT);
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
  // TODO MG documentation above "[m/s]" and variable names "vel" not consistent with ...
  // ... method calling in SlamLocalizationStep ...
  // ... where parameters are called rougheningLinAccelStd etc. (accel vs. vel!).
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
    for (int index = 0; index < slamParticles.length; ++index) {
      double linVel = SlamRandomUtil.getTruncatedGaussian(linVelAvg, linVelStd, LINVEL_MIN, LINVEL_MAX);
      double maxAngVel = TURN_RATE_PER_METER * linVel;
      double minAngVel = -maxAngVel;
      double angVel = SlamRandomUtil.getTruncatedGaussian(0, angVelStd, minAngVel, maxAngVel);
      slamParticles[index].initialize(pose, RealScalar.of(linVel), RealScalar.of(angVel), initLikelihood);
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
  @SuppressWarnings("unused")
  private static void multinomialResampling(SlamParticle[] slamParticles) {
    int numbOfPart = slamParticles.length;
    // assigned particle numbers start at zero
    int[] assignedPart = new int[slamParticles.length];
    // generate array with cumulative particle probabilities
    double[] particleCDF = new double[numbOfPart];
    for (int i = 1; i < numbOfPart; i++)
      particleCDF[i] = particleCDF[i - 1] + slamParticles[i].getParticleLikelihood();
    // draw as many random numbers as particles and find corresponding CDF number
    double[] randomNumbers = new double[numbOfPart];
    SlamRandomUtil.setUniformRVArray(randomNumbers);
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

  /** non-standard resampling method
   * 
   * @param slamParticles */
  private static void neglectLowLikelihoods(SlamParticle[] slamParticles) {
    final double initLikelihood = 1.0 / slamParticles.length;
    // sort by likelihood
    Arrays.parallelSort(slamParticles, SlamParticleLikelihoodComparator.INSTANCE);
    final int middle = slamParticles.length / 2;
    // duplicate half of particles with highest likelihood
    for (int index = middle; index < slamParticles.length; index++)
      slamParticles[index].setStateFromParticle(slamParticles[index - middle], initLikelihood);
    // reset likelihoods of remaining particles
    for (int index = 0; index < middle; ++index)
      slamParticles[index].setParticleLikelihood(initLikelihood);
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

  /** disturbs the linVel state with Gaussian noise while not violating
   * the linear acceleration limits of the vehicle
   * 
   * function uses hard-coded acceleration and velocity limits
   * 
   * @param oldLinVel current linVel state
   * @param linVelRougheningStd interpreted as [m/s] standard deviation of additive Gaussian noise
   * @param dT interpreted as [s]
   * @return updated disturbed linVel */
  private static double limitLinAccel(double oldLinVel, double linVelRougheningStd, double dT) {
    double maxAccel = 2.5; // TODO MG declare above like LINVEL_MAX etc.
    double minAccel = -2.5; // ... here also
    double linAccel = SlamRandomUtil.getTruncatedGaussian(0, linVelRougheningStd, minAccel, maxAccel);
    double newLinVel = oldLinVel + linAccel * dT;
    if (LINVEL_MAX < newLinVel)
      return LINVEL_MAX;
    if (newLinVel < LINVEL_MIN)
      return LINVEL_MIN;
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
    double minVel = -TURN_RATE_PER_METER * oldLinVel;
    double maxVel = -minVel;
    double angAccel = SlamRandomUtil.getTruncatedGaussian(0, angVelRougheningStd, minAccel, maxAccel);
    double newAngVel = oldAngVel + angAccel * dT;
    if (newAngVel < minVel)
      return minVel;
    if (newAngVel > maxVel)
      return maxVel;
    return newAngVel;
  }

  public static void printStatusInfo(SlamParticle[] slamParticles) {
    Arrays.parallelSort(slamParticles, SlamParticleLikelihoodComparator.INSTANCE);
    System.out.println("**** new status info **********");
    for (int i = 0; i < slamParticles.length; i++)
      System.out.println("Particle likelihood " + slamParticles[i].getParticleLikelihood());
  }
}
