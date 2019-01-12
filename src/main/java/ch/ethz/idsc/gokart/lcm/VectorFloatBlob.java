// code by ynager
package ch.ethz.idsc.gokart.lcm;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Primitives;
import idsc.BinaryBlob;

/** publish functionality was first used 20180427
 * on that date the byte order was not yet set to LITTLE_ENDIAN
 * 
 * publishes and receives tensor as vector of floats
 * 
 * @see ArrayFloatBlob */
public enum VectorFloatBlob {
  ;
  /** @param tensor
   * @return */
  public static BinaryBlob encode(Tensor tensor) {
    float[] data = Primitives.toFloatArray(tensor);
    BinaryBlob binaryBlob = BinaryBlobs.create(4 * data.length);
    ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    for (float value : data)
      byteBuffer.putFloat(value);
    return binaryBlob;
  }

  /** @param byteBuffer
   * @return */
  public static Tensor decode(ByteBuffer byteBuffer) {
    FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
    return Tensor.of(IntStream.range(0, floatBuffer.remaining()) //
        .mapToObj(index -> DoubleScalar.of(floatBuffer.get())));
  }
}
