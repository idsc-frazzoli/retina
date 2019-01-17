// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import ch.ethz.idsc.gokart.core.AutoboxScheduledProvider;
import ch.ethz.idsc.gokart.dev.linmot.LinmotConfig;
import ch.ethz.idsc.gokart.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.gokart.dev.linmot.LinmotPutOperation;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetListener;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.util.data.SoftWatchdog;
import ch.ethz.idsc.retina.util.data.Watchdog;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;

/** module controls the brake with rank {@link ProviderRank#EMERGENCY}
 * 
 * the module
 * <ul>
 * <li>tracks the tangent speed of the vehicle from the wheel odometry
 * <li>receives the distance to an obstacle along the x-axis
 * <li>schedules the press of the brake for the estimated duration that is required to stop the vehicle
 * </ul> */
public final class EmergencyBrakeProvider extends AutoboxScheduledProvider<LinmotPutEvent> implements RimoGetListener {
  // TODO magic const to filter slip
  private static final Clip CLIP = Clip.function( //
      Quantity.of(0, SI.VELOCITY), //
      Quantity.of(6, SI.VELOCITY));
  // ---
  public static final EmergencyBrakeProvider INSTANCE = new EmergencyBrakeProvider();
  // ---
  /** unit m*s^-1 */
  private final Scalar minVelocity = LinmotConfig.GLOBAL.minVelocity;
  /** without unit but with interpretation in meter */
  // TODO JPH make function SensorsConfig.GLOBAL.vlp16.Get(0)
  private final Scalar margin = ChassisGeometry.GLOBAL.xTipMeter().subtract(SensorsConfig.GLOBAL.vlp16.Get(0));
  private final Watchdog watchdog = SoftWatchdog.barking(0.1);
  private Scalar velocity = Quantity.of(0.0, SI.VELOCITY);

  private EmergencyBrakeProvider() {
    CLIP.requireInside(velocity);
  }

  @Override // from PutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.EMERGENCY;
  }

  @Override // from RimoGetListener
  public void getEvent(RimoGetEvent rimoGetEvent) {
    velocity = CLIP.apply(ChassisGeometry.GLOBAL.odometryTangentSpeed(rimoGetEvent));
  }

  public void pacify() {
    watchdog.notifyWatchdog();
  }

  /** @param min without unit but with interpretation in meter from lidar */
  // TODO JPH expect unit [m]
  public void consider(Scalar min) {
    if (watchdog.isBarking() && Scalars.lessEquals(minVelocity, velocity) && isIdle()) {
      EmergencyBrakeManeuver emergencyBrakeManeuver = LinmotConfig.GLOBAL.brakeDistance(velocity);
      if (emergencyBrakeManeuver.isRequired(Quantity.of(min.subtract(margin), SI.METER)))
        schedule();
    }
  }

  @Override // from AutoboxScheduledProvider
  protected void protected_schedule() {
    EmergencyBrakeManeuver emergencyBrakeManeuver = LinmotConfig.GLOBAL.brakeDistance(velocity);
    long timestamp = now_ms();
    long duration_ms = emergencyBrakeManeuver.getDuration_ms();
    eventUntil( //
        timestamp += duration_ms, //
        () -> LinmotPutOperation.INSTANCE.toRelativePosition(RealScalar.ONE));
  }

  /** @return distance from lidar to front bumper of gokart along x-axis
   * without unit but with interpretation in meter */
  Scalar marginMeter() {
    return margin;
  }
}
