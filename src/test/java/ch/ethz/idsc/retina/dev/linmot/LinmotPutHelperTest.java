// code by jph
package ch.ethz.idsc.retina.dev.linmot;

import junit.framework.TestCase;

public class LinmotPutHelperTest extends TestCase {
  public void testSimple() {
    assertTrue(LinmotPutHelper.FALLBACK_OPERATION.isOperational());
  }
}
