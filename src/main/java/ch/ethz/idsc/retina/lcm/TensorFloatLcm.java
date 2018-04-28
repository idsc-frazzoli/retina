// code by ynager
package ch.ethz.idsc.retina.lcm;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Primitives;

/** publish functionality was first used 20180427
 * on that date the byte order was not yet set to LITTLE_ENDIAN
 * 
 * publishes and receives tensor as vector of floats */
public enum TensorFloatLcm {
  ;
  /** @param channel
   * @param tensor */
  public static void publish(String channel, Tensor tensor) {
    BinaryBlobPublisher binaryBlobPublisher = new BinaryBlobPublisher(channel);
    binaryBlobPublisher.accept(toByteArray(tensor));
  }

  /** @param byteBuffer
   * @return */
  public static Tensor receive(ByteBuffer byteBuffer) {
    FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
    return Tensor.of(IntStream.range(0, floatBuffer.remaining()) //
        .mapToObj(index -> DoubleScalar.of(floatBuffer.get())));
  }

  /* package */ static byte[] toByteArray(Tensor tensor) {
    float[] data = Primitives.toFloatArray(tensor);
    byte[] array = new byte[4 * data.length];
    ByteBuffer byteBuffer = ByteBuffer.wrap(array);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    for (float value : data)
      byteBuffer.putFloat(value);
    return array;
  }
}
