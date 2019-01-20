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
import junit.framework.TestCase;

public class PowerLookupTableTest extends TestCase {
  public void testMinMax() throws Exception {
    Scalar currentErrorLimit = Quantity.of(2, NonSI.ARMS);
    Scalar correctMaxCurr = Quantity.of(2300, NonSI.ARMS);
    PowerLookupTable powerLookupTable = PowerLookupTable.getInstance();
    Scalar velocity = Quantity.of(3, SI.VELOCITY);
    Tensor minMax = powerLookupTable.getMinMaxAcceleration(velocity);
    assertEquals(QuantityUnit.of(minMax.Get(0)), SI.ACCELERATION);
    assertTrue(Scalars.lessThan(minMax.Get(0).negate(), minMax.Get(1)));
    // System.out.println("minMax=" + minMax);
    Scalar minpower = RealScalar.ONE.negate();
    Scalar maxpower = RealScalar.ONE;
    Scalar maxacc = powerLookupTable.getNormalizedAcceleration(maxpower, velocity);
    // System.out.println(maxacc);
    Scalar maxcurr = powerLookupTable.getNeededCurrent(maxacc, velocity);
    Scalar minacc = powerLookupTable.getNormalizedAcceleration(minpower, velocity);
    Scalar mincurr = powerLookupTable.getNeededCurrent(minacc, velocity);
    // assertTrue(Scalars.lessThan(mincurr.add(correctMaxCurr).abs(), currentErrorLimit));
    // assertTrue(Scalars.lessThan(maxcurr.subtract(correctMaxCurr).abs(), currentErrorLimit));
    // System.out.println(maxacc);
    // System.out.println(minacc);
    // System.out.println(maxcurr);
    // System.out.println(mincurr);
    Scalar torquelessAcc = powerLookupTable.getNormalizedAccelerationTorqueCentered(//
        RealScalar.of(0), velocity);
    Scalar torquelessCurr = powerLookupTable.getNeededCurrent(torquelessAcc, velocity);
    // System.out.println(torquelessAcc);
    // System.out.println(torquelessCurr);
    assertTrue(Scalars.lessThan(torquelessCurr.abs(), currentErrorLimit));
  }
}