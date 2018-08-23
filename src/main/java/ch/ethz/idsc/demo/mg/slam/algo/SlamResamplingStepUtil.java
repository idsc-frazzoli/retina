// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.Arrays;

import ch.ethz.idsc.demo.mg.slam.SlamParticle;
import ch.ethz.idsc.demo.mg.util.slam.SlamParticleLikelihoodComparator;
import ch.ethz.idsc.demo.mg.util.slam.SlamRandomUtil;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.RealScalar;

/* package */ enum SlamResamplingStepUtil {
  ;
  private static final double TURN_RATE_PER_METER = //
      Magnitude.PER_METER.toDouble(SteerConfig.GLOBAL.turningRatioMax);
  private static final double LINVEL_MIN = 0; // "m/s"
  private static final double LINVEL_MAX = 8; // "m/s"
  private static final double LINACCEL_MIN = -2.5; // "m/s²"
  private static final double LINACCEL_MAX = 2.5; // "m/s²"
  private static final double ANGACCEL_MIN = -6; // "rad/s²"
  private static final double ANGACCEL_MAX = 6; // "rad/s²"

  /** particle resampling. multinominal sampling and neglect_low_likelihood method are available. particle roughening is also provided
   * 
   * @param slamParticles
   * @param dT interpreted as [s]
   * @param rougheningLinAccelStd interpreted as [m/s²] standard deviation of additive Gaussian noise for particle roughening
   * @param rougheningAngAccelStd interpreted as [rad/s²] standard deviation of additive Gaussian noise for particle roughening */
  public static void resampleParticles(SlamParticle[] slamParticles, double dT, double rougheningLinAccelStd, double rougheningAngAccelStd) {
    // SlamParticleUtil.multinomialSampling(slamParticles);
    SlamResamplingStepUtil.neglectLowLikelihoods(slamParticles);
    // depending on resampling method, roughening might be used
    SlamResamplingStepUtil.particleRoughening(slamParticles, dT, rougheningLinAccelStd, rougheningAngAccelStd);
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
   * @param rougheningLinAccelStd interpreted as [m/s²] standard deviation of additive Gaussian noise
   * @param rougheningAngAccelStd interpreted as [rad/s²] standard deviation of additive Gaussian noise */
  private static void particleRoughening(SlamParticle[] slamParticles, double dT, double rougheningLinAccelStd, double rougheningAngAccelStd) {
    for (int i = 0; i < slamParticles.length; i++) {
      double linVel = limitLinAccel(slamParticles[i].getLinVelDouble(), rougheningLinAccelStd, dT);
      double angVel = limitAngAccel(slamParticles[i].getAngVelDouble(), slamParticles[i].getLinVelDouble(), rougheningAngAccelStd, dT);
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
   * @param rougheningLinAccelStd interpreted as [m/s²] standard deviation of additive Gaussian noise
   * @param dT interpreted as [s]
   * @return updated disturbed linVel */
  private static double limitLinAccel(double oldLinVel, double rougheningLinAccelStd, double dT) {
    double linAccel = SlamRandomUtil.getTruncatedGaussian(0, rougheningLinAccelStd, LINACCEL_MIN, LINACCEL_MAX);
    double newLinVel = oldLinVel + linAccel * dT;
    if (LINVEL_MAX < newLinVel)
      return LINVEL_MAX;
    if (newLinVel < LINVEL_MIN)
      return LINVEL_MIN;
    return newLinVel;
  }

  /** disturbs the angVel state with Gaussian noise while not violating the angular acceleration limits of the vehicle
   * 
   * function used hard-coded acceleration limits
   * 
   * @param oldAngVel current angVel state
   * @param oldLinVel current linVel state
   * @param rougheningAngAccelStd interpreted as [rad/s²] standard deviation of additive Gaussian noise
   * @param dT interpreted as [s]
   * @return updated disturbed angVel */
  private static double limitAngAccel(double oldAngVel, double oldLinVel, double rougheningAngAccelStd, double dT) {
    double maxVel = TURN_RATE_PER_METER * oldLinVel;
    double minVel = -maxVel;
    double angAccel = SlamRandomUtil.getTruncatedGaussian(0, rougheningAngAccelStd, ANGACCEL_MIN, ANGACCEL_MAX);
    double newAngVel = oldAngVel + angAccel * dT;
    if (newAngVel < minVel)
      return minVel;
    if (newAngVel > maxVel)
      return maxVel;
    return newAngVel;
  }
}
