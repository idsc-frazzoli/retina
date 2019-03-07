// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.demo.mg.slam.config.SlamDvsConfig;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.TruncatedGaussian;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ enum SlamCoreContainerUtil {
  ;
  private static final double TURN_RATE_PER_METER = //
      Magnitude.PER_METER.toDouble(SteerConfig.GLOBAL.turningRatioMax);
  // ---
  private static final double LINVEL_AVG = Magnitude.VELOCITY.toDouble(SlamDvsConfig.eventCamera.slamCoreConfig.linVelAvg);
  private static final double LINVEL_STD = Magnitude.VELOCITY.toDouble(SlamDvsConfig.eventCamera.slamCoreConfig.linVelStd);
  private static final double ANGVEL_STD = Magnitude.PER_SECOND.toDouble(SlamDvsConfig.eventCamera.slamCoreConfig.angVelStd);
  private static final TruncatedGaussian TRUNCATED_GAUSSIAN = //
      new TruncatedGaussian(LINVEL_AVG, LINVEL_STD, VehicleConfig.LINVEL_MIN, VehicleConfig.LINVEL_MAX);

  /** initial distribution of slamParticles with a given pose and Gaussian distributed linear and angular velocities
   * 
   * @param slamParticles
   * @param pose {[m],[m],[-]} initial pose which is identical for all particles
   * @param LINVEL_AVG interpreted as [m/s] average initial linear velocity
   * @param LINVEL_STD interpreted as [m/s] standard deviation of linear velocity
   * @param ANGVEL_STD interpreted as [rad/s] standard deviation of angular velocity. initial angular velocity is set to 0 */
  public static void setInitialDistribution(SlamParticle[] slamParticles, Tensor pose) {
    final double initLikelihood = 1.0 / slamParticles.length;
    for (int index = 0; index < slamParticles.length; ++index) {
      double linVel = TRUNCATED_GAUSSIAN.nextValue();
      double maxAngVel = TURN_RATE_PER_METER * linVel;
      // handle the case that maxAngVel is very close to zero
      // TODO instead of 0.2, use a value that guarantees with high probability that TruncatedGaussian will find value (probably smaller)
      final double angVel = maxAngVel < 0.2 * ANGVEL_STD //
          ? 0.0
          : new TruncatedGaussian(0, ANGVEL_STD, -maxAngVel, maxAngVel).nextValue();
      slamParticles[index].initialize(pose, RealScalar.of(linVel), RealScalar.of(angVel), initLikelihood);
    }
  }
}
