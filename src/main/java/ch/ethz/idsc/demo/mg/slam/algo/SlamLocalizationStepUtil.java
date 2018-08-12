// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.Arrays;

import ch.ethz.idsc.demo.mg.slam.SlamParticle;
import ch.ethz.idsc.demo.mg.util.slam.SlamParticleLikelihoodComparator;
import ch.ethz.idsc.demo.mg.util.slam.SlamRandomUtil;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.qty.Quantity;

enum SlamLocalizationStepUtil {
  ;
  /** max turning rate per meter
   * 
   * https://github.com/idsc-frazzoli/retina/files/1958724/20180429_minimum_turning_radius.pdf */
  public static final Scalar TURNING_RATIO_MAX = Quantity.of(0.4082, "rad*m^-1");

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

  /** Careful: ordering of given array slamParticles is subject to change
   * 
   * get average pose of particles in relevant range
   * 
   * @param slamParticles
   * @param relevantRange [-] number of particles with highest likelihood that is used
   * @return averagePose unitless representation */
  public static Tensor getAveragePose(SlamParticle[] slamParticles, int relevantRange) {
    // TODO MG the sorting does not move the most relevant to the front! instead would have to sort over entire list?
    // ... comment on what relevantRange is intended to represent
    Arrays.parallelSort(slamParticles, 0, relevantRange, SlamParticleLikelihoodComparator.INSTANCE);
    double likelihoodSum = 0;
    Tensor expectedPose = Array.zeros(3);
    for (int i = 0; i < relevantRange; ++i) {
      double likelihood = slamParticles[i].getParticleLikelihood();
      likelihoodSum += likelihood;
      Tensor pose = slamParticles[i].getPoseUnitless();
      expectedPose = expectedPose.add(pose.multiply(RealScalar.of(likelihood)));
    }
    // TODO MG comment on case likelihoodSum == 0, why not possible?
    return expectedPose.divide(RealScalar.of(likelihoodSum));
  }
}
