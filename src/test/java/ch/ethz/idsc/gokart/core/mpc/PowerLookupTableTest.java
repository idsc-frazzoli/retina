// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class PowerLookupTableTest extends TestCase {
  public void testMinMax() throws Exception {
    Scalar currentErrorLimit = Quantity.of(2, NonSI.ARMS);
    Scalar correctMaxCurr = Quantity.of(2300, NonSI.ARMS);
    PowerLookupTable powerLookupTable = PowerLookupTable.getInstance();
    Scalar velocity = Quantity.of(3, SI.VELOCITY);
    Scalar maxpower = RealScalar.ONE;
    Scalar minpower = RealScalar.ONE.negate();
    Scalar maxacc = powerLookupTable.getNormalizedAcceleration(maxpower, velocity);
    Scalar maxcurr = powerLookupTable.getNeededCurrent(maxacc, velocity);
    Scalar minacc = powerLookupTable.getNormalizedAcceleration(minpower, velocity);
    Scalar mincurr = powerLookupTable.getNeededCurrent(minacc, velocity);
    assertTrue(Scalars.lessThan(mincurr.add(correctMaxCurr).abs(), currentErrorLimit));
    assertTrue(Scalars.lessThan(maxcurr.subtract(correctMaxCurr).abs(), currentErrorLimit));
    System.out.println(maxacc);
    System.out.println(minacc);
    System.out.println(maxcurr);
    System.out.println(mincurr);
    Scalar torquelessAcc = powerLookupTable.getNormalizedAccelerationTorqueCentered(//
        Quantity.of(0, SI.ONE), velocity);
    Scalar torquelessCurr = powerLookupTable.getNeededCurrent(torquelessAcc, velocity);
    System.out.println(torquelessAcc);
    System.out.println(torquelessCurr);
    assertTrue(Scalars.lessThan(torquelessCurr.abs(), currentErrorLimit));
  }
}