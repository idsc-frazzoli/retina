// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import ch.ethz.idsc.gokart.dev.linmot.LinmotConfig;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class EmergencyBrakeManeuverTest extends TestCase {
  public void testSimple() {
    EmergencyBrakeManeuver emergencyBrakeManeuver = //
        LinmotConfig.GLOBAL.brakeDistance(Quantity.of(6, SI.VELOCITY));
    assertTrue(emergencyBrakeManeuver.isRequired(Quantity.of(3, SI.METER)));
    assertFalse(emergencyBrakeManeuver.isRequired(Quantity.of(8, SI.METER)));
  }

  public void testMore() {
    EmergencyBrakeManeuver emergencyBrakeManeuver = //
        LinmotConfig.GLOBAL.brakeDistance(Quantity.of(4, SI.VELOCITY));
    assertTrue(emergencyBrakeManeuver.isRequired(Quantity.of(2, SI.METER)));
    assertFalse(emergencyBrakeManeuver.isRequired(Quantity.of(4, SI.METER)));
  }

  public void testZero() {
    EmergencyBrakeManeuver emergencyBrakeManeuver = //
        LinmotConfig.GLOBAL.brakeDistance(Quantity.of(0, SI.VELOCITY));
    assertTrue(Scalars.isZero(emergencyBrakeManeuver.distance));
    Sign.requirePositiveOrZero(emergencyBrakeManeuver.duration);
  }

  public void testPositive() {
    EmergencyBrakeManeuver emergencyBrakeManeuver = //
        new EmergencyBrakeManeuver(Quantity.of(1, "s"), Quantity.of(-4, SI.ACCELERATION), Quantity.of(2, SI.VELOCITY));
    assertEquals(emergencyBrakeManeuver.distance, Scalars.fromString("5/2[m]"));
    assertEquals(emergencyBrakeManeuver.duration, Scalars.fromString("3/2[s]"));
  }

  public void testNegative() {
    EmergencyBrakeManeuver emergencyBrakeManeuver = //
        new EmergencyBrakeManeuver(Quantity.of(1, "s"), Quantity.of(4, SI.ACCELERATION), Quantity.of(-2, SI.VELOCITY));
    assertEquals(emergencyBrakeManeuver.distance, Scalars.fromString("-5/2[m]"));
    assertEquals(emergencyBrakeManeuver.duration, Scalars.fromString("3/2[s]"));
  }

  public void testSpecs() {
    for (int delay = 0; delay < 5; ++delay) {
      Scalar responseTime = Quantity.of(delay, "s");
      Scalar maxDeceleration = Quantity.of(-2, "m*s^-2");
      Scalar velocity = Quantity.of(10, "m*s^-1");
      EmergencyBrakeManeuver emergencyBrakeManeuver = //
          new EmergencyBrakeManeuver(responseTime, maxDeceleration, velocity);
      assertEquals(emergencyBrakeManeuver.distance, Quantity.of(25 + delay * 10, SI.METER));
      assertEquals(emergencyBrakeManeuver.duration, Quantity.of(5 + delay, SI.SECOND));
      assertEquals(emergencyBrakeManeuver.getDuration_ms(), (5 + delay) * 1000);
    }
  }

  public void testFail() {
    Scalar responseTime = Quantity.of(1, SI.SECOND);
    Scalar maxDeceleration = Quantity.of(-2, "m*s^-2");
    Scalar velocity = Quantity.of(10, "m*s");
    try {
      new EmergencyBrakeManeuver(responseTime, maxDeceleration, velocity);
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
