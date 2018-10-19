// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import junit.framework.TestCase;

public class LookUpTable2DTest extends TestCase {
  public void testMinMax() throws Exception {
    PowerLookupTable powerLookupTable = PowerLookupTable.getInstance();
    Scalar velocity = Quantity.of(5, SI.VELOCITY);
    Scalar maxpower = RealScalar.ONE;
    Scalar minpower = RealScalar.ONE.negate();
    Scalar maxacc = powerLookupTable.getNormalizedAcceleration(maxpower, velocity).multiply(Quantity.of(1.02, SI.ONE));
    Scalar maxcurr = powerLookupTable.getNeededCurrent(maxacc, velocity);
    Scalar currtest = powerLookupTable.getNeededCurrent(Quantity.of(2, SI.ACCELERATION), velocity);
    System.out.println(currtest);
    Scalar minacc = powerLookupTable.getNormalizedAcceleration(minpower, velocity);
    Scalar mincurr = powerLookupTable.getNeededCurrent(minacc, velocity);
    System.out.println(maxacc);
    System.out.println(minacc);
    System.out.println(maxcurr);
    System.out.println(mincurr);
  }
}