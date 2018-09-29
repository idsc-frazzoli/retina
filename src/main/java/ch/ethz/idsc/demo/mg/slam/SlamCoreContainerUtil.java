// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.demo.mg.slam.config.SlamCoreConfig;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.TruncatedGaussian;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ enum SlamCoreContainerUtil {
  ;
  private static final double TURN_RATE_PER_METER = //
      Magnitude.PER_METER.toDouble(SteerConfig.GLOBAL.turningRatioMax);
  private static final double linVelAvg = Magnitude.VELOCITY.toDouble(SlamCoreConfig.GLOBAL.linVelAvg);
  private static final double linVelStd = Magnitude.VELOCITY.toDouble(SlamCoreConfig.GLOBAL.linVelStd);
  private static final double angVelStd = Magnitude.PER_SECOND.toDouble(SlamCoreConfig.GLOBAL.angVelStd);

  /** initial distribution of slamParticles with a given pose and Gaussian distributed linear and angular velocities
   * 
   * @param slamParticles
   * @param pose {[m],[m],[-]} initial pose which is identical for all particles
   * @param linVelAvg interpreted as [m/s] average initial linear velocity
   * @param linVelStd interpreted as [m/s] standard deviation of linear velocity
   * @param angVelStd interpreted as [rad/s] standard deviation of angular velocity. initial angular velocity is set to 0 */
  public static void setInitialDistribution(SlamParticle[] slamParticles, Tensor pose) {
    final TruncatedGaussian truncatedGaussian = new TruncatedGaussian(linVelAvg, linVelStd, VehicleConfig.LINVEL_MIN, VehicleConfig.LINVEL_MAX);
    final double initLikelihood = 1.0 / slamParticles.length;
    for (int index = 0; index < slamParticles.length; ++index) {
      double linVel = truncatedGaussian.nextValue();
      double maxAngVel = TURN_RATE_PER_METER * linVel;
      // handle the case that maxAngVel is very close to zero
      // TODO instead of 0.2, use a value that guarantees with high probability that TruncatedGaussian will find value (probably smaller)
      final double angVel = maxAngVel < 0.2 * angVelStd //
          ? 0.0
          : new TruncatedGaussian(0, angVelStd, -maxAngVel, maxAngVel).nextValue();
      slamParticles[index].initialize(pose, RealScalar.of(linVel), RealScalar.of(angVel), initLikelihood);
    }
  }
}
