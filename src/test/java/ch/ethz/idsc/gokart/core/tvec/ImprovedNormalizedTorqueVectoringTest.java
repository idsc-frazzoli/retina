// code by mh
package ch.ethz.idsc.gokart.core.tvec;

import ch.ethz.idsc.gokart.calib.power.PowerLookupTable;
import ch.ethz.idsc.gokart.core.man.ManualConfig;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.red.Norm;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class ImprovedNormalizedTorqueVectoringTest extends TestCase {
  private static final PowerLookupTable POWER_LOOKUP_TABLE = PowerLookupTable.getInstance();

  public void testZeros() {
    TorqueVectoringConfig torqueVectoringConfig = new TorqueVectoringConfig();
    torqueVectoringConfig.staticCompensation = Quantity.of(0.4, SI.ACCELERATION.negate());
    torqueVectoringConfig.dynamicCorrection = Quantity.of(0, SI.SECOND);
    TorqueVectoringInterface torqueVectoringInterface = new ImprovedNormalizedTorqueVectoring(torqueVectoringConfig);
    Scalar power = RealScalar.ZERO;
    Tensor powers = torqueVectoringInterface.powers( //
        Quantity.of(0, "m^-1"), //
        Quantity.of(0, "m*s^-1"), //
        Quantity.of(0, "s^-1"), //
        power, Quantity.of(0, "s^-1"));
    Chop._08.requireClose(Total.of(powers), power);
    assertEquals(powers, Tensors.vector(0, 0));
  }

  public void testZeroMean() {
    // this is only true when we have no torque vectoring
    TorqueVectoringConfig torqueVectoringConfig = new TorqueVectoringConfig();
    torqueVectoringConfig.staticCompensation = Quantity.of(0.4, SI.ACCELERATION.negate());
    torqueVectoringConfig.dynamicCorrection = Quantity.of(0, SI.SECOND);
    TorqueVectoringInterface torqueVectoringInterface = new ImprovedNormalizedTorqueVectoring(torqueVectoringConfig);
    Scalar power = RealScalar.ZERO;
    Scalar velocity = Quantity.of(1, SI.VELOCITY);
    Tensor powers = torqueVectoringInterface.powers( //
        Quantity.of(1, "m^-1"), //
        velocity, //
        Quantity.of(0, "s^-1"), //
        power, Quantity.of(0, "s^-1"));
    // more complicated test
    Scalar maxcurr = ManualConfig.GLOBAL.torqueLimit;
    Scalar noPowerAcceleration = POWER_LOOKUP_TABLE.getAcceleration(Quantity.of(0, NonSI.ARMS), Quantity.of(1, "m*s^-1"));
    Scalar leftAcc = POWER_LOOKUP_TABLE.getAcceleration(powers.Get(0).multiply(maxcurr), velocity);
    Scalar rightAcc = POWER_LOOKUP_TABLE.getAcceleration(powers.Get(1).multiply(maxcurr), velocity);
    Chop._04.requireClose(Mean.of(Tensors.of(leftAcc, rightAcc)), noPowerAcceleration);
    // assertEquals(powers, Tensors.vector(-0.4, 0.4));
  }

  public void testSaturatedPositive() {
    TorqueVectoringConfig torqueVectoringConfig = new TorqueVectoringConfig();
    torqueVectoringConfig.staticCompensation = Quantity.of(0.4, SI.ACCELERATION.negate());
    torqueVectoringConfig.dynamicCorrection = Quantity.of(0, SI.SECOND);
    TorqueVectoringInterface torqueVectoringInterface = new ImprovedNormalizedTorqueVectoring(torqueVectoringConfig);
    Scalar power = RealScalar.ONE;
    Tensor powers = torqueVectoringInterface.powers( //
        Quantity.of(1, "m^-1"), //
        Quantity.of(-2, "m*s^-1"), //
        Quantity.of(3, "s^-1"), //
        power, Quantity.of(0, "s^-1"));
    // it's 0.9999999...
    Scalar between = Norm._2.between(powers, Tensors.vector(1, 1));
    assertTrue(Scalars.lessThan(between, RealScalar.of(0.02)));
    // assertTrue(Chop._04.close(powers, Tensors.vector(1, 1)));
  }

  public void testSaturatedNegative() {
    TorqueVectoringConfig torqueVectoringConfig = new TorqueVectoringConfig();
    torqueVectoringConfig.staticCompensation = Quantity.of(0.4, SI.ACCELERATION.negate());
    torqueVectoringConfig.dynamicCorrection = Quantity.of(0, SI.SECOND);
    TorqueVectoringInterface torqueVectoringInterface = new ImprovedNormalizedTorqueVectoring(torqueVectoringConfig);
    Scalar power = RealScalar.ONE.negate();
    Tensor powers = torqueVectoringInterface.powers( //
        Quantity.of(1, "m^-1"), //
        Quantity.of(-2, "m*s^-1"), //
        Quantity.of(3, "s^-1"), //
        power, Quantity.of(0, "s^-1"));
    Scalar between = Norm._2.between(powers, Tensors.vector(-1, -1));
    assertTrue(Scalars.lessThan(between, RealScalar.of(0.02)));
  }

  /* Scalar expectedRotationPerMeterDriven
   * Scalar meanTangentSpeed
   * Scalar angularSlip
   * Scalar power
   * Scalar realRotation */
  public void testTorqueWhenUndersteering() {
    TorqueVectoringConfig torqueVectoringConfig = new TorqueVectoringConfig();
    torqueVectoringConfig.staticCompensation = Quantity.of(0.4, SI.ACCELERATION.negate());
    torqueVectoringConfig.dynamicCorrection = Quantity.of(0.2, SI.SECOND);
    TorqueVectoringInterface torqueVectoringInterface = new ImprovedNormalizedTorqueVectoring(torqueVectoringConfig);
    Scalar power = RealScalar.ZERO;
    Tensor powers = torqueVectoringInterface.powers( //
        Quantity.of(1, "m^-1"), //
        Quantity.of(3, "m*s^-1"), //
        Quantity.of(1, "s^-1"), //
        power,
        // brutal oversteering -> reaction should be that there is no differential torque
        Quantity.of(0, "s^-1"));
    assertTrue(Scalars.lessThan(powers.Get(0), powers.Get(1)));
  }

  public void testNoTorqueWhenOversteering() {
    TorqueVectoringConfig torqueVectoringConfig = new TorqueVectoringConfig();
    torqueVectoringConfig.staticCompensation = Quantity.of(0.4, SI.ACCELERATION.negate());
    torqueVectoringConfig.dynamicCorrection = Quantity.of(0.2, SI.SECOND);
    TorqueVectoringInterface torqueVectoringInterface = new ImprovedNormalizedTorqueVectoring(torqueVectoringConfig);
    Scalar power = RealScalar.ZERO;
    Tensor powers = torqueVectoringInterface.powers( //
        Quantity.of(-1, "m^-1"), //
        Quantity.of(3, "m*s^-1"), //
        Quantity.of(1, "s^-1"), //
        power,
        // brutal oversteering -> reaction should be that there is no differential torque
        Quantity.of(3, "s^-1"));
    assertEquals(powers.Get(0), powers.Get(1));
  }
}
