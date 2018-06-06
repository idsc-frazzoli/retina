// code by jph
package ch.ethz.idsc.retina.dev.linmot;

import ch.ethz.idsc.gokart.core.fuse.EmergencyBrakeManeuver;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

/** values inspired by 20180217_emergency_braking.pdf */
public class LinmotConfigTest extends TestCase {
  private static void _check(Number v, Number duration, Number distance) {
    EmergencyBrakeManeuver brakeDistance = LinmotConfig.GLOBAL.brakeDistance(Quantity.of(v, SI.VELOCITY));
    assertEquals( //
        brakeDistance.duration, //
        Quantity.of(duration, SI.SECOND));
    assertEquals( //
        brakeDistance.distance, //
        Quantity.of(distance, SI.METER));
  }

  public void testSimple() {
    _check(0, 0.15, 0);
    _check(4.5, 1 + 0.15, 2.925);
    _check(6, 4. / 3. + 0.15, 4.9);
  }
}
