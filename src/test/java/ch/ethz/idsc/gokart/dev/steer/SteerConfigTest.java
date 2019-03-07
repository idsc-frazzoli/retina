// code by jph
package ch.ethz.idsc.gokart.dev.steer;

import ch.ethz.idsc.gokart.calib.steer.SteerMapping;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.math.SIDerived;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityMagnitude;
import ch.ethz.idsc.tensor.qty.QuantityUnit;
import ch.ethz.idsc.tensor.qty.Unit;
import ch.ethz.idsc.tensor.qty.UnitSystem;
import ch.ethz.idsc.tensor.sca.Clip;
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

  public void testSCEfromAngle() {
    SteerMapping steerMapping = SteerConfig.GLOBAL.getSteerMapping();
    Scalar q = steerMapping.getSCEfromAngle(Quantity.of(1, "rad"));
    assertEquals(QuantityUnit.of(q), Unit.of("SCE"));
    assertTrue(1.1 < q.number().doubleValue());
  }

  public void testAngleLimit() {
    Clip clip = SteerConfig.GLOBAL.getAngleLimit();
    assertEquals(clip.min(), clip.max().negate());
    clip.requireInside(Quantity.of(0.37, SIDerived.RADIAN));
  }

  public void testConversion() {
    Scalar radius = UnitSystem.SI().apply(SteerConfig.GLOBAL.turningRatioMax.reciprocal());
    Clip clip = Clip.function(Quantity.of(2.4, SI.METER), Quantity.of(2.5, SI.METER));
    assertTrue(clip.isInside(radius));
  }

  public void testTurningAtLimit() {
    // according to our model
    Scalar ratio_unitless = Magnitude.PER_METER.apply(SteerConfig.GLOBAL.turningRatioMax);
    Scalar angle = ChassisGeometry.GLOBAL.steerAngleForTurningRatio(ratio_unitless);
    // angle == 0.4521892315592385[rad]
    SteerMapping steerMapping = SteerConfig.GLOBAL.getSteerMapping();
    Scalar encoder = steerMapping.getSCEfromAngle(angle);
    // encoder == 0.7536487192653976[SCE]
    // our simple, linear steering model tells us an encoder value outside the max range
    // conclusion: we should build a more accurate model that maps [encoder <-> effective steering angle]
    Clip clip = Clip.function(Quantity.of(0.5, SteerPutEvent.UNIT_ENCODER), Quantity.of(0.8, SteerPutEvent.UNIT_ENCODER));
    assertTrue(clip.isInside(encoder));
  }
}
