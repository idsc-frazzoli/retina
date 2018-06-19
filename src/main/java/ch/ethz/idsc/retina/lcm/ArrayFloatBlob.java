// code by jph
package ch.ethz.idsc.retina.lcm;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.ArrayReshape;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.io.Primitives;
import idsc.BinaryBlob;

/** encode and decode tensors with array structure of arbitrary rank with float precision */
public enum ArrayFloatBlob {
  ;
  private static final BinaryOperator<Integer> PRODUCT = (a, b) -> a * b;

  private static int numel(Stream<Integer> stream) {
    return stream.reduce(PRODUCT).orElse(1);
  }

  /** @param tensor of arbitrary rank with array structure
   * @return
   * @throws Exception if given tensor does not have array structure */
  public static BinaryBlob encode(Tensor tensor) {
    List<Integer> dims = Dimensions.of(tensor);
    int numel = numel(dims.stream());
    BinaryBlob binaryBlob = BinaryBlobs.create(Byte.BYTES + dims.size() * Integer.BYTES + numel * Float.BYTES);
    ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.put((byte) dims.size()); // rank
    dims.stream().forEach(byteBuffer::putInt);
    Primitives.toStreamNumber(tensor) //
        .forEach(number -> byteBuffer.putFloat(number.floatValue()));
    return binaryBlob;
  }

  /** @param byteBuffer
   * @return */
  public static Tensor decode(ByteBuffer byteBuffer) {
    int rank = byteBuffer.get() & 0xff;
    Integer[] dims = new Integer[rank];
    IntStream.range(0, rank).forEach(i -> dims[i] = byteBuffer.getInt());
    int numel = numel(Stream.of(dims));
    return ArrayReshape.of( //
        IntStream.range(0, numel).mapToObj(i -> DoubleScalar.of(byteBuffer.getFloat())), dims);
  }
}
