// code by jph
package ch.ethz.idsc.gokart.calib.brake;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.qty.QuantityUnit;
import junit.framework.TestCase;

public class BrakeFunctionConfigTest extends TestCase {
  public void testUnits() {
    assertEquals(QuantityUnit.of(BrakeFunctionConfig.GLOBAL.decelerationThreshold), SI.ACCELERATION);
    assertEquals(QuantityUnit.of(BrakeFunctionConfig.GLOBAL.speedThreshold), SI.VELOCITY);
    assertEquals(QuantityUnit.of(BrakeFunctionConfig.GLOBAL.lockupRatio), SI.ONE);
    assertEquals(QuantityUnit.of(BrakeFunctionConfig.GLOBAL.geodesicFilterAlpha), SI.ONE);
  }
}
