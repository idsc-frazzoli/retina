// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.demo.mg.util.slam.SlamRandomUtil;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ enum SlamContainerUtil {
  ;
  // TODO JPH are already defined in SlamLocalizationStepUtil, how to reuse?
  private static final double TURN_RATE_PER_METER = //
      Magnitude.PER_METER.toDouble(SteerConfig.GLOBAL.turningRatioMax);
  private static final double LINVEL_MIN = 0; // "m/s"
  private static final double LINVEL_MAX = 8; // "m/s"

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
}
