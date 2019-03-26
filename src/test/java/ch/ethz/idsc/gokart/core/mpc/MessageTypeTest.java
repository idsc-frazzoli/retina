// code by jph
package ch.ethz.idsc.gokart.core.mpc;

import junit.framework.TestCase;

public class MessageTypeTest extends TestCase {
  public void testOrdinals() {
    assertEquals(MessageType.CONTROL_REQUEST.ordinal(), 0);
    assertEquals(MessageType.PATH_PARAMETER.ordinal(), 1);
    assertEquals(MessageType.OPTIMIZATION_PARAMETER_KINEMATIC.ordinal(), 2);
    assertEquals(MessageType.CONTROL_PREDICTION.ordinal(), 3);
    assertEquals(MessageType.OPTIMIZATION_PARAMETER_DYNAMIC.ordinal(), 4);
  }
}
