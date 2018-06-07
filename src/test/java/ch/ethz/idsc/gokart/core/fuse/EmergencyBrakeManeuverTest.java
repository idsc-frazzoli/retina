// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import ch.ethz.idsc.retina.dev.linmot.LinmotConfig;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class EmergencyBrakeManeuverTest extends TestCase {
  public void testSimple() {
    EmergencyBrakeManeuver emergencyBrakeManeuver = LinmotConfig.GLOBAL.brakeDistance(Quantity.of(6, SI.VELOCITY));
    assertTrue(emergencyBrakeManeuver.isRequired(Quantity.of(3, SI.METER)));
    assertFalse(emergencyBrakeManeuver.isRequired(Quantity.of(8, SI.METER)));
  }
}
