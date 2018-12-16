// code by mg
package ch.ethz.idsc.demo.mg.slam.core;

import java.util.Arrays;

import ch.ethz.idsc.demo.mg.slam.SlamParticle;
import ch.ethz.idsc.demo.mg.slam.VehicleConfig;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.TruncatedGaussian;
import ch.ethz.idsc.tensor.RealScalar;

/* package */ class SlamResamplingStepUtil {
  private static final double TURN_RATE_PER_METER = //
      Magnitude.PER_METER.toDouble(SteerConfig.GLOBAL.turningRatioMax);
  // ---
  private final TruncatedGaussian tgLinAccel;
  private final TruncatedGaussian tgAngAccel;

  public SlamResamplingStepUtil(double rougheningLinAccelStd, double rougheningAngAccelStd) {
    tgLinAccel = new TruncatedGaussian(0, rougheningLinAccelStd, VehicleConfig.LINACCEL_MIN, VehicleConfig.LINACCEL_MAX);
    tgAngAccel = new TruncatedGaussian(0, rougheningAngAccelStd, VehicleConfig.ANGACCEL_MIN, VehicleConfig.ANGACCEL_MAX);
  }

  /** particle resampling using neglect_low_likelihood method. After resampling, a particle roughening step is executed.
   * 
   * @param slamParticles
   * @param dT interpreted as [s] */
  public void resampleParticles(SlamParticle[] slamParticles, double dT) {
    neglectLowLikelihoods(slamParticles);
    particleRoughening(slamParticles, dT);
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

  /** disturbs the particle states with truncated Gaussian noise
   * 
   * @param slamParticles
   * @param dT interpreted as [s] */
  private void particleRoughening(SlamParticle[] slamParticles, double dT) {
    for (int i = 0; i < slamParticles.length; i++) {
      double linVel = limitLinAccel(slamParticles[i].getLinVelDouble(), dT);
      double angVel = limitAngAccel(slamParticles[i].getAngVelDouble(), slamParticles[i].getLinVelDouble(), dT);
      slamParticles[i].setLinVel(RealScalar.of(linVel));
      slamParticles[i].setAngVel(RealScalar.of(angVel));
    }
  }

  /** disturbs the linVel state with Gaussian noise while not violating
   * the linear acceleration limits of the vehicle
   * 
   * @param oldLinVel current linVel state
   * @param dT interpreted as [s]
   * @return updated disturbed linVel */
  private double limitLinAccel(double oldLinVel, double dT) {
    double linAccel = tgLinAccel.nextValue();
    double newLinVel = oldLinVel + linAccel * dT;
    if (VehicleConfig.LINVEL_MAX < newLinVel)
      return VehicleConfig.LINVEL_MAX;
    if (newLinVel < VehicleConfig.LINVEL_MIN)
      return VehicleConfig.LINVEL_MIN;
    return newLinVel;
  }

  /** disturbs the angVel state with Gaussian noise while not violating the angular acceleration limits of the vehicle
   * 
   * @param oldAngVel current angVel state
   * @param oldLinVel current linVel state
   * @param dT interpreted as [s]
   * @return updated disturbed angVel */
  private double limitAngAccel(double oldAngVel, double oldLinVel, double dT) {
    double maxVel = TURN_RATE_PER_METER * oldLinVel;
    double minVel = -maxVel;
    double angAccel = tgAngAccel.nextValue();
    double newAngVel = oldAngVel + angAccel * dT;
    if (newAngVel < minVel)
      return minVel;
    if (newAngVel > maxVel)
      return maxVel;
    return newAngVel;
  }
}
