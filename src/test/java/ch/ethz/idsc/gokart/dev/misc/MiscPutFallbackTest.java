// code by jph
package ch.ethz.idsc.gokart.dev.misc;

import java.util.Arrays;
import java.util.Optional;

import junit.framework.TestCase;

public class MiscPutFallbackTest extends TestCase {
  public void testRegistered() {
    Optional<MiscPutEvent> optional = MiscPutFallback.INSTANCE.putEvent();
    MiscPutEvent mpe = optional.get();
    assertEquals(mpe.length(), 6);
    assertTrue(Arrays.equals(mpe.asArray(), new byte[6]));
  }
}
