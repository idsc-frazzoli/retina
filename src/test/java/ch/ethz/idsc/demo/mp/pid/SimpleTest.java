package ch.ethz.idsc.demo.mp.pid;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Round;
import junit.framework.TestCase;
 
public class SimpleTest extends TestCase{
  public void testSimple() {
    Scalar aScalar = Quantity.of(10, SI.METER);
    Scalar bScalar = Quantity.of(2, SI.PER_SECOND);
    Scalar vScalar = Quantity.of(12, SI.VELOCITY);
    
    System.out.println(aScalar.multiply(bScalar).subtract(vScalar).divide(vScalar).map(Magnitude.ONE).map(Round._4));
    
  }
}
