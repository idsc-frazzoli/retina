// code by jph
package ch.ethz.idsc.gokart.dev.steer;

import java.util.Arrays;
import java.util.Optional;

import junit.framework.TestCase;

public class SteerPutFallbackTest extends TestCase {
  public void testRegistered() {
    Optional<SteerPutEvent> optional = SteerPutFallback.INSTANCE.putEvent();
    assertTrue(optional.isPresent());
    byte[] array = optional.get().asArray();
    Arrays.equals(array, new byte[5]);
  }
}
