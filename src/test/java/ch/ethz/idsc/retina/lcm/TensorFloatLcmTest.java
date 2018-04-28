// code by ynager
package ch.ethz.idsc.retina.lcm;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class TensorFloatLcmTest extends TestCase {
  public void testSimple() {
    Tensor vector = Tensors.fromString("{1[A],2.34[s*y^-1],{2,3,4,{4[A*V]}}}");
    TensorFloatLcm.publish("test_publisher", vector);
  }

  public void testReceive() {
    Tensor vector = Tensors.fromString("{1[A],2.34[s*y^-1],{2,3,4,{4[A*V]}}}");
    byte[] array = TensorFloatLcm.toByteArray(vector);
    ByteBuffer byteBuffer = ByteBuffer.wrap(array);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    Tensor tensor = TensorFloatLcm.receive(byteBuffer);
    assertTrue(Chop._10.close(tensor, Tensors.fromString("{1.0, 2.3399999141693115, 2.0, 3.0, 4.0, 4.0}")));
  }
}
