// code by jph
package ch.ethz.idsc.gokart.lcm;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.sca.Chop;
import idsc.BinaryBlob;
import junit.framework.TestCase;

public class ArrayFloatBlobTest extends TestCase {
  public void testSimple() {
    assertEquals(Byte.BYTES, 1);
    assertEquals(Short.BYTES, 2);
    assertEquals(Float.BYTES, 4);
  }

  public void testEncodeDecode1() {
    Distribution distribution = NormalDistribution.standard();
    Tensor tensor = RandomVariate.of(distribution, 140);
    assertFalse(Chop._04.allZero(tensor));
    BinaryBlob binaryBlob = ArrayFloatBlob.encode(tensor);
    assertEquals(binaryBlob.data_length, 1 + 1 * 4 + 140 * 4);
    ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    Tensor result = ArrayFloatBlob.decode(byteBuffer);
    assertTrue(Chop._06.close(tensor, result));
  }

  public void testEncodeDecode2() {
    Distribution distribution = NormalDistribution.standard();
    Tensor tensor = RandomVariate.of(distribution, 40, 3);
    assertFalse(Chop._04.allZero(tensor));
    BinaryBlob binaryBlob = ArrayFloatBlob.encode(tensor);
    assertEquals(binaryBlob.data_length, 1 + 2 * 4 + 120 * 4);
    ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    Tensor result = ArrayFloatBlob.decode(byteBuffer);
    assertTrue(Chop._06.close(tensor, result));
  }

  public void testEncodeDecode3() {
    Distribution distribution = NormalDistribution.standard();
    Tensor tensor = RandomVariate.of(distribution, 2, 3, 4);
    assertFalse(Chop._04.allZero(tensor));
    BinaryBlob binaryBlob = ArrayFloatBlob.encode(tensor);
    assertEquals(binaryBlob.data_length, 1 + 3 * 4 + 24 * 4);
    ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    Tensor result = ArrayFloatBlob.decode(byteBuffer);
    assertTrue(Chop._06.close(tensor, result));
  }
}
