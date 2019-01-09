// code by jph
package ch.ethz.idsc.gokart.dev.linmot;

import ch.ethz.idsc.gokart.core.fuse.EmergencyBrakeManeuver;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.UnitSystem;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

/** values inspired by 20180217_emergency_braking.pdf */
public class LinmotConfigTest extends TestCase {
  public void testTempClip() {
    Clip clip = LinmotConfig.GLOBAL.temperatureHardwareClip();
    Clip maxrange = Clip.function(Quantity.of(90, NonSI.DEGREE_CELSIUS), Quantity.of(120, NonSI.DEGREE_CELSIUS));
    maxrange.requireInside(clip.max());
  }

  private static void requireClose(Scalar a, Scalar b) {
    if (!Chop._06.close(a, b)) {
      throw TensorRuntimeException.of(a, b);
    }
  }

  public static void testSimple(Scalar velocity, Scalar distance, Scalar duration) {
    Scalar responseTime = Quantity.of(0.25, "s");
    Scalar maxDeceleration = Quantity.of(-4, "m*s^-2");
    EmergencyBrakeManeuver emergencyBrakeManeuver = //
        new EmergencyBrakeManeuver(responseTime, maxDeceleration, velocity);
    requireClose(emergencyBrakeManeuver.distance, distance);
    requireClose(emergencyBrakeManeuver.duration, duration);
  }

  public void testTypical() {
    testSimple(Quantity.of(1, "m*s^-1"), Scalars.fromString("0.375[m]"), Scalars.fromString("0.50[s]"));
    testSimple(Quantity.of(2, "m*s^-1"), Scalars.fromString("1.000[m]"), Scalars.fromString("0.75[s]"));
    testSimple(Quantity.of(4, "m*s^-1"), Scalars.fromString("3.000[m]"), Scalars.fromString("1.25[s]"));
    testSimple(Quantity.of(5, "m*s^-1"), Scalars.fromString("4.375[m]"), Scalars.fromString("1.50[s]"));
  }

  public void testMinVel() {
    Scalar minVel = UnitSystem.SI().apply(LinmotConfig.GLOBAL.minVelocity);
    Clip clip = Clip.function(Quantity.of(0.2, SI.VELOCITY), Quantity.of(0.4, SI.VELOCITY));
    clip.requireInside(minVel);
    // Sign.requirePositive(minVel);
  }

  public void testRangeResponseTime() {
    Clip clip = Clip.function(Quantity.of(0.1, SI.SECOND), Quantity.of(0.3, SI.SECOND));
    clip.requireInside(UnitSystem.SI().apply(LinmotConfig.GLOBAL.responseTime));
  }

  public void testRangeMaxDecl() {
    Clip clip = Clip.function(Quantity.of(-5, SI.ACCELERATION), Quantity.of(-3.5, SI.ACCELERATION));
    clip.requireInside(UnitSystem.SI().apply(LinmotConfig.GLOBAL.maxDeceleration));
  }
}
