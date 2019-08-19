// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class RimoAxleConstantsTest extends TestCase {
  public void testSome() {
    Tensor pair_unit = RimoAxleConstants.getDifferentialSpeed().pair(RealScalar.ONE, RealScalar.of(0.1));
    Tensor pair_meas = Tensors.vector(0.9497016064634988, 1.040306724092553);
    Chop._08.requireClose(pair_unit, pair_meas);
    pair_meas.dot(pair_unit).Get().multiply(RealScalar.of(0.5));
    // System.out.println(speed);
  }
}
