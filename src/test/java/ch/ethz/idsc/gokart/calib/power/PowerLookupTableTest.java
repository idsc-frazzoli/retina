// code by mh
package ch.ethz.idsc.gokart.calib.power;

import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.QuantityUnit;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class PowerLookupTableTest extends TestCase {
  private final PowerLookupTable powerLookupTable = PowerLookupTable.getInstance();
  private final Scalar velocity = Quantity.of(3, SI.VELOCITY);

  public void testGeneral() {
    Tensor minMax = powerLookupTable.getMinMaxAcceleration(velocity);
    assertEquals(QuantityUnit.of(minMax.Get(0)), SI.ACCELERATION);
    assertTrue(Scalars.lessThan(minMax.Get(0).negate(), minMax.Get(1)));
  }

  public void testMax() {
    Scalar maxpower = RealScalar.ONE;
    Scalar maxacc = powerLookupTable.getNormalizedAcceleration(Quantity.of(0, SI.ACCELERATION), maxpower, velocity);
    Chop._05.requireClose(maxacc, Scalars.fromString("2.098241985797882[m*s^-2]"));
    Scalar maxcurr = powerLookupTable.getNeededCurrent(maxacc, velocity);
    Chop._02.requireClose(maxcurr, Quantity.of(1269.2182511091232, NonSI.ARMS));
  }

  public void testMin() {
    Scalar minpower = RealScalar.ONE.negate();
    Scalar minacc = powerLookupTable.getNormalizedAcceleration(Quantity.of(0, SI.ACCELERATION), minpower, velocity);
    Chop._06.requireClose(minacc, Scalars.fromString("-1.6670369653701782[m*s^-2]"));
    Scalar mincurr = powerLookupTable.getNeededCurrent(minacc, velocity);
    Chop._03.requireClose(mincurr, Quantity.of(-2315, "ARMS"));
  }

  public void testErrorLimit() {
    Scalar torquelessAcc = powerLookupTable.getNormalizedAccelerationTorqueCentered(//
        RealScalar.of(0), velocity);
    Scalar torquelessCurr = powerLookupTable.getNeededCurrent(torquelessAcc, velocity);
    Scalar currentErrorLimit = Quantity.of(2, NonSI.ARMS);
    assertTrue(Scalars.lessThan(torquelessCurr.abs(), currentErrorLimit));
  }
}