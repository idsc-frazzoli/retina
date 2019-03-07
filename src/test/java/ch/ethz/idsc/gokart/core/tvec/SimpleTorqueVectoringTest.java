// code by mh
package ch.ethz.idsc.gokart.core.tvec;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class SimpleTorqueVectoringTest extends TestCase {
  public void testZeros() {
    TorqueVectoringConfig torqueVectoringConfig = new TorqueVectoringConfig();
    torqueVectoringConfig.staticCompensation = Quantity.of(0.4, SI.ACCELERATION.negate());
    torqueVectoringConfig.dynamicCorrection = Quantity.of(0, SI.SECOND);
    TorqueVectoringInterface torqueVectoringInterface = new SimpleTorqueVectoring(torqueVectoringConfig);
    Scalar power = RealScalar.ZERO;
    Tensor powers = torqueVectoringInterface.powers( //
        Quantity.of(0, "m^-1"), //
        Quantity.of(0, "m*s^-1"), //
        Quantity.of(0, "s^-1"), //
        power, Quantity.of(0, "s^-1"));
    assertTrue(Chop._08.close(Total.of(powers), power));
    assertEquals(powers, Tensors.vector(0, 0));
  }

  public void testZeroMean() {
    TorqueVectoringConfig torqueVectoringConfig = new TorqueVectoringConfig();
    torqueVectoringConfig.staticCompensation = Quantity.of(0.4, SI.ACCELERATION.negate());
    torqueVectoringConfig.dynamicCorrection = Quantity.of(0, SI.SECOND);
    TorqueVectoringInterface torqueVectoringInterface = new SimpleTorqueVectoring(torqueVectoringConfig);
    Scalar power = RealScalar.ZERO;
    Tensor powers = torqueVectoringInterface.powers( //
        Quantity.of(1, "m^-1"), //
        Quantity.of(1, "m*s^-1"), //
        Quantity.of(1, "s^-1"), //
        power, Quantity.of(0, "s^-1"));
    assertTrue(Chop._08.close(Total.of(powers), power));
    assertEquals(powers, Tensors.vector(-0.4, 0.4));
  }

  public void testSaturatedPositive() {
    TorqueVectoringConfig torqueVectoringConfig = new TorqueVectoringConfig();
    torqueVectoringConfig.staticCompensation = Quantity.of(0.4, SI.ACCELERATION.negate());
    torqueVectoringConfig.dynamicCorrection = Quantity.of(0, SI.SECOND);
    TorqueVectoringInterface torqueVectoringInterface = new SimpleTorqueVectoring(torqueVectoringConfig);
    Scalar power = RealScalar.ONE;
    Tensor powers = torqueVectoringInterface.powers( //
        Quantity.of(1, "m^-1"), //
        Quantity.of(-2, "m*s^-1"), //
        Quantity.of(3, "s^-1"), //
        power, Quantity.of(0, "s^-1"));
    assertEquals(powers, Tensors.vector(1, 1));
  }

  public void testSaturatedNegative() {
    TorqueVectoringConfig torqueVectoringConfig = new TorqueVectoringConfig();
    torqueVectoringConfig.staticCompensation = Quantity.of(0.4, SI.ACCELERATION.negate());
    torqueVectoringConfig.dynamicCorrection = Quantity.of(0, SI.SECOND);
    TorqueVectoringInterface torqueVectoringInterface = new SimpleTorqueVectoring(torqueVectoringConfig);
    Scalar power = RealScalar.ONE.negate();
    Tensor powers = torqueVectoringInterface.powers( //
        Quantity.of(1, "m^-1"), //
        Quantity.of(-2, "m*s^-1"), //
        Quantity.of(3, "s^-1"), //
        power, Quantity.of(0, "s^-1"));
    assertEquals(powers, Tensors.vector(-1, -1));
  }
}
