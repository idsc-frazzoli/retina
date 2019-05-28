// code by jph
package ch.ethz.idsc.gokart.dev.steer;

import ch.ethz.idsc.gokart.calib.steer.RimoAxleConstants;
import ch.ethz.idsc.gokart.calib.steer.SteerMapping;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.qty.QuantityUnit;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.qty.UnitSystem;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class SteerConfigTest extends TestCase {
  public void testSimple() {
    Scalar q = Quantity.of(2, "km*NOU");
    Scalar r = QuantityMagnitude.SI().in(Unit.of("m*NOU")).apply(q);
    assertEquals(r, RealScalar.of(2000));
  }

  public void testSCE() {
    assertEquals(QuantityUnit.of(SteerConfig.GLOBAL.columnMax), Unit.of("SCE"));
  }

  public void testAngleLimit() {
    Clip clip = SteerConfig.GLOBAL.getRatioLimit();
    assertEquals(clip.min(), clip.max().negate());
    clip.requireInside(Quantity.of(0.37, SI.PER_METER));
  }

  public void testConversion() {
    Scalar radius = UnitSystem.SI().apply(SteerConfig.GLOBAL.turningRatioMax.reciprocal());
    Clip clip = Clips.interval(Quantity.of(2.12, SI.METER), Quantity.of(2.4, SI.METER));
    assertTrue(clip.isInside(radius));
  }

  public void testTurningAtLimit() {
    // according to our model
    Scalar angle = RimoAxleConstants.steerAngleForTurningRatio(SteerConfig.GLOBAL.turningRatioMax);
    // angle == 0.45218923155923850 ante 20190509
    // angle == 0.49164265965082177 post 20190509
    // System.out.println(angle);
    Clips.interval(0.48, 0.5).requireInside(angle);
  }

  public void testTurningAtLimitCubic() {
    SteerMapping steerMapping = SteerConfig.GLOBAL.getSteerMapping();
    Scalar encoder = steerMapping.getSCEfromRatio(SteerConfig.GLOBAL.turningRatioMax);
    // encoder == 0.6561921674138146[SCE]
    // our simple, linear steering model tells us an encoder value outside the max range
    // conclusion: we should build a more accurate model that maps [encoder <-> effective steering angle]
    Clip clip = Clips.interval(Quantity.of(0.5, SteerPutEvent.UNIT_ENCODER), SteerConfig.GLOBAL.columnMax);
    assertTrue(clip.isInside(encoder));
  }
}
