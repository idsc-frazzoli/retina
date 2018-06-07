// code by jph
package ch.ethz.idsc.retina.dev.linmot;

import ch.ethz.idsc.gokart.core.fuse.EmergencyBrakeManeuver;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

/** values inspired by 20180217_emergency_braking.pdf */
public class LinmotConfigTest extends TestCase {
  private static void requireClose(Scalar a, Scalar b, double eps) {
    if (!Chop.below(eps).close(a, b)) {
      throw TensorRuntimeException.of(a, b);
    }
  }

  private static void _check(Number v, Number duration, Number distance) {
    EmergencyBrakeManeuver emergencyBrakeManeuver = //
        LinmotConfig.GLOBAL.brakeDistance(Quantity.of(v, SI.VELOCITY));
    requireClose( //
        emergencyBrakeManeuver.duration, //
        Quantity.of(duration, SI.SECOND), 0.2);
    requireClose( //
        emergencyBrakeManeuver.distance, //
        Quantity.of(distance, SI.METER), 1.5);
    long du = emergencyBrakeManeuver.getDuration_ms();
    du -= duration.doubleValue() * 1000;
    assertTrue(-500 < du && du < 500);
  }

  public void testSimple() {
    _check(0, 0.3, 0);
    _check(4.5, 1.425, 3.88125);
    _check(6, 1.8, 6.3);
  }

  public void testMinVel() {
    Scalar minVel = Magnitude.VELOCITY.apply(LinmotConfig.GLOBAL.minVelocity);
    Sign.requirePositive(minVel);
  }
}
