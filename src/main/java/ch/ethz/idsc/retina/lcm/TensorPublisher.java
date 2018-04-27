// code by ynager
package ch.ethz.idsc.retina.lcm;

import java.nio.ByteBuffer;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Primitives;

public enum TensorPublisher {
  ;
  public static void publish(String channel, Tensor tensor) {
    float[] data = Primitives.toFloatArray(tensor);
    byte[] array = new byte[4 * data.length];
    ByteBuffer buffer = ByteBuffer.wrap(array);
    for (Float value : data)
      buffer.putFloat(value);
    BinaryBlobPublisher publisher = new BinaryBlobPublisher(channel);
    publisher.accept(array);
  }
}
