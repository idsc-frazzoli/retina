// code by ynager
package ch.ethz.idsc.retina.lcm;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class TensorPublisherTest extends TestCase {
  public void testSimple() {
    Tensor vector = Tensors.fromString("{1[A],2.34[s*y^-1],{2,3,4,{4[A*V]}}}");
    // System.out.println(vector.multiply(RealScalar.of(123)).divide(Quantity.of(123, "s")));
    TensorPublisher.publish("test_publisher", vector);
  }
}
