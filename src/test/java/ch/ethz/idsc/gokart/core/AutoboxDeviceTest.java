// code by jph
package ch.ethz.idsc.gokart.core;

import java.util.Objects;

import junit.framework.TestCase;

public class AutoboxDeviceTest extends TestCase {
  public void testSimple() {
    // System.out.println(AutoboxDevice.REMOTE_INET_ADDRESS);
    assertTrue(Objects.nonNull(AutoboxDevice.REMOTE_INET_ADDRESS)); // "/192.168.1.10"
  }

  public void testNullMessage() {
    RuntimeException runtimeException = new RuntimeException((String) null);
    try {
      throw runtimeException;
    } catch (RuntimeException e) {
      // ---
    }
  }
}
