// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.demo.mg.util.slam.SlamRandomUtil;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;

/** container for the objects that are modified by the SLAM algorithm */
public class SlamContainer {
  private static final double TURN_RATE_PER_METER = //
      Magnitude.PER_METER.toDouble(SteerConfig.GLOBAL.turningRatioMax);
  private static final double LINVEL_MIN = 0; // "m/s"
  private static final double LINVEL_MAX = 8; // "m/s"
  // ---
  private final SlamEstimatedPose slamEstimatedPose;
  private final SlamParticle[] slamParticles;
  private final MapProvider occurrenceMap;
  private final double linVelAvg;
  private final double linVelStd;
  private final double angVelStd;

  public SlamContainer(SlamConfig slamConfig) {
    slamEstimatedPose = new SlamEstimatedPose();
    int numOfPart = slamConfig.numberOfParticles.number().intValue();
    slamParticles = new SlamParticle[numOfPart];
    for (int index = 0; index < numOfPart; ++index)
      slamParticles[index] = new SlamParticle();
    occurrenceMap = new MapProvider(slamConfig);
    linVelAvg = Magnitude.VELOCITY.toDouble(slamConfig.linVelAvg);
    linVelStd = Magnitude.VELOCITY.toDouble(slamConfig.linVelStd);
    angVelStd = Magnitude.PER_SECOND.toDouble(slamConfig.angVelStd);
  }

  public void initialize(Tensor initPose) {
    setInitialDistribution(slamParticles, initPose, linVelAvg, linVelStd, angVelStd);
  }

  public SlamParticle[] getSlamParticles() {
    return slamParticles;
  }

  public MapProvider getOccurrenceMap() {
    return occurrenceMap;
  }

  public SlamEstimatedPose getSlamEstimatedPose() {
    return slamEstimatedPose;
  }

  // TODO MG move to static helper
  /** initial distribution of slamParticles with a given pose and Gaussian distributed linear and angular velocities
   * 
   * @param slamParticles
   * @param pose {[m],[m],[-]} initial pose which is identical for all particles
   * @param linVelAvg [m/s] average initial linear velocity
   * @param linVelStd [m/s] standard deviation of linear velocity
   * @param angVelStd [rad/s] standard deviation of angular velocity. initial angular velocity is set to 0 */
  private static void setInitialDistribution(SlamParticle[] slamParticles, Tensor pose, double linVelAvg, double linVelStd, double angVelStd) {
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
