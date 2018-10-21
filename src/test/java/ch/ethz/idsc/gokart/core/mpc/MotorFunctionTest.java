// code by jph
package ch.ethz.idsc.gokart.core.mpc;

import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class MotorFunctionTest extends TestCase {
  public void testSimple() {
    Scalar epos = MotorFunction.getAccelerationEstimation( //
        Quantity.of(+1000, NonSI.ARMS), //
        Quantity.of(5, SI.VELOCITY));
    assertTrue(Chop._10.close(epos, Scalars.fromString("1.0026400089263916[m*s^-2]")));
    // TODO expected accel to be "less" ?
    MotorFunction.getAccelerationEstimation( //
        Quantity.of(-1000, NonSI.ARMS), //
        Quantity.of(-5, SI.VELOCITY));
    // System.out.println(eneg);
    // assertTrue(Chop._10.close(epos, Scalars.fromString("1.0026400089263916[m*s^-2]")));
  }
}
