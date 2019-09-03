// code by jph
package ch.ethz.idsc.gokart.core.mpc;

import junit.framework.TestCase;

public class MPCNativeSessionTest extends TestCase {
  public void testSimple() {
    MPCNativeSession mpcNativeSession = new MPCNativeSession();
    assertEquals(mpcNativeSession.nextMessageId(MessageType.CONTROL_REQUEST), 0);
    assertEquals(mpcNativeSession.nextMessageId(MessageType.CONTROL_REQUEST), 1);
    assertEquals(mpcNativeSession.nextMessageId(MessageType.OPTIMIZATION_PARAMETER_DYNAMIC), 0);
    assertEquals(mpcNativeSession.nextMessageId(MessageType.CONTROL_REQUEST), 2);
  }
}
