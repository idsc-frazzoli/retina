// code by jph
package ch.ethz.idsc.gokart.calib.power;

import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityUnit;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class MotorFunctionV2Test extends TestCase {
  public void testSimple() {
    Scalar epos = MotorFunctionV2.getAccelerationEstimation( //
        Quantity.of(+1000, NonSI.ARMS), //
        Quantity.of(5, SI.VELOCITY));
    assertEquals(QuantityUnit.of(epos), SI.ACCELERATION);
    Chop._04.requireClose(epos, Scalars.fromString("1.4792[m*s^-2]"));
    Scalar eneg = MotorFunctionV2.getAccelerationEstimation( //
        Quantity.of(-1000, NonSI.ARMS), //
        Quantity.of(-5, SI.VELOCITY));
    Chop._12.requireClose(epos, eneg.negate());
  }

  public void testHigh() {
    Scalar acc = MotorFunctionV2.getAccelerationEstimation( //
        Quantity.of(+2100, NonSI.ARMS), //
        Quantity.of(5, SI.VELOCITY));
    assertEquals(QuantityUnit.of(acc), SI.ACCELERATION);
    Chop._04.requireClose(acc, Scalars.fromString("2.0357[m*s^-2]"));
  }

  public void testLow() {
    Scalar acc = MotorFunctionV2.getAccelerationEstimation( //
        Quantity.of(-2100, NonSI.ARMS), //
        Quantity.of(5, SI.VELOCITY));
    assertEquals(QuantityUnit.of(acc), SI.ACCELERATION);
    Chop._04.requireClose(acc, Scalars.fromString("-1.6929[m*s^-2]"));
  }

  public void testZero() {
    Scalar acc = MotorFunctionV2.getAccelerationEstimation( //
        Quantity.of(0, NonSI.ARMS), //
        Quantity.of(0, SI.VELOCITY));
    assertEquals(QuantityUnit.of(acc), SI.ACCELERATION);
    Chop._04.requireClose(acc, Scalars.fromString("0[m*s^-2]"));
  }

  public void testSymmetry() {
    for (int i = 0; i < 1000; i++) {
      Scalar power = Quantity.of(RandomVariate.of(UniformDistribution.of(-2300, 2300)), NonSI.ARMS);
      Scalar velocity = Quantity.of(RandomVariate.of(UniformDistribution.of(-10, 10)), SI.VELOCITY);
      Scalar epos = MotorFunctionV2.getAccelerationEstimation( //
          power, //
          velocity);
      assertEquals(QuantityUnit.of(epos), SI.ACCELERATION);
      Scalar eneg = MotorFunctionV2.getAccelerationEstimation( //
          power.negate(), //
          velocity.negate());
      if (!Chop._05.close(epos, eneg.negate())) {
        System.out.println(power + "/" + velocity + ": " + epos + "/" + eneg);
        fail();
      }
    }
  }
}
