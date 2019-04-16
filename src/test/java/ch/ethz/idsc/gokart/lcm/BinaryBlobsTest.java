// code by jph
package ch.ethz.idsc.gokart.lcm;

import java.util.Arrays;

import idsc.BinaryBlob;
import junit.framework.TestCase;

public class BinaryBlobsTest extends TestCase {
  public void testCreate() {
    BinaryBlob binaryBlob = BinaryBlobs.create(123);
    assertEquals(binaryBlob.data_length, 123);
    assertEquals(binaryBlob.data.length, 123);
    assertTrue(Arrays.equals(binaryBlob.data, new byte[123]));
  }
}
