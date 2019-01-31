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
    Scalar eneg = MotorFunction.getAccelerationEstimation( //
        Quantity.of(-1000, NonSI.ARMS), //
        Quantity.of(-5, SI.VELOCITY));
    Chop._12.requireClose(epos, eneg.negate());
  }

  public void testSymmetry() {
    for (int i = 0; i < 1000; i++) {
      Scalar power = Quantity.of(RandomVariate.of(UniformDistribution.of(-2300, 2300)), NonSI.ARMS);
      Scalar velocity = Quantity.of(RandomVariate.of(UniformDistribution.of(-10, 10)), SI.VELOCITY);
      Scalar epos = MotorFunction.getAccelerationEstimation( //
          power, //
          velocity);
      assertEquals(QuantityUnit.of(epos), SI.ACCELERATION);
      Scalar eneg = MotorFunction.getAccelerationEstimation( //
          power.negate(), //
          velocity.negate());
      System.out.println(power + "/" + velocity + ": " + epos + "/" + eneg);
      Chop._05.requireClose(epos, eneg.negate());
    }
  }
}
