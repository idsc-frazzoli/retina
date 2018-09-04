// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.TruncatedGaussian;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ enum SlamContainerUtil {
  ;
  private static final double TURN_RATE_PER_METER = //
      Magnitude.PER_METER.toDouble(SteerConfig.GLOBAL.turningRatioMax);

  /** initial distribution of slamParticles with a given pose and Gaussian distributed linear and angular velocities
   * 
   * @param slamParticles
   * @param pose {[m],[m],[-]} initial pose which is identical for all particles
   * @param linVelAvg [m/s] average initial linear velocity
   * @param linVelStd [m/s] standard deviation of linear velocity
   * @param angVelStd [rad/s] standard deviation of angular velocity. initial angular velocity is set to 0 */
  public static void setInitialDistribution(SlamParticle[] slamParticles, Tensor pose, final double linVelAvg, final double linVelStd, double angVelStd) {
    final TruncatedGaussian truncatedGaussian = new TruncatedGaussian(linVelAvg, linVelStd, VehicleConfig.LINVEL_MIN, VehicleConfig.LINVEL_MAX);
    final double initLikelihood = 1.0 / slamParticles.length;
    for (int index = 0; index < slamParticles.length; ++index) {
      double linVel = truncatedGaussian.nextValue();
      double maxAngVel = TURN_RATE_PER_METER * linVel;
      double minAngVel = -maxAngVel;
      // FIXME MG if angVelStd is large and minAngVel, maxAngVel are very small then
      // ... the next line cannot find a solution:
      double angVel = new TruncatedGaussian(0, angVelStd, minAngVel, maxAngVel).nextValue();
      slamParticles[index].initialize(pose, RealScalar.of(linVel), RealScalar.of(angVel), initLikelihood);
    }
  }
}
