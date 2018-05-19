// code by jph
package ch.ethz.idsc.retina.util.data;

import junit.framework.TestCase;

public class BooleanParserTest extends TestCase {
  public void testCase() {
    assertNull(BooleanParser.orNull("False"));
  }
}
