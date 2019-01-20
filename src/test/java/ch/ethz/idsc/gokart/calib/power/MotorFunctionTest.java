// code by jph
package ch.ethz.idsc.gokart.calib.power;

import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityUnit;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class MotorFunctionTest extends TestCase {
  public void testSfpos() {
    float sfpos = MotorFunction.sfpos(.3f, .5f);
    assertEquals(sfpos, -0.28409305f);
  }

  public void testSfneg() {
    float sfneg = MotorFunction.sfneg(.3f, .5f);
    assertEquals(sfneg, -0.3884523f);
  }

  public void testSimple() {
    Scalar epos = MotorFunction.getAccelerationEstimation( //
        Quantity.of(+1000, NonSI.ARMS), //
        Quantity.of(5, SI.VELOCITY));
    assertEquals(QuantityUnit.of(epos), SI.ACCELERATION);
    Chop._10.requireClose(epos, Scalars.fromString("1.0026400089263916[m*s^-2]"));
    // TODO MH expected accel to be "less" ?
    MotorFunction.getAccelerationEstimation( //
        Quantity.of(-1000, NonSI.ARMS), //
        Quantity.of(-5, SI.VELOCITY));
  }
}
