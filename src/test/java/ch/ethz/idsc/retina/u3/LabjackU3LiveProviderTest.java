// code by jph
package ch.ethz.idsc.retina.u3;

import junit.framework.TestCase;

public class LabjackU3LiveProviderTest extends TestCase {
  public void testParse() {
    float[] array = LabjackU3LiveProvider.parse("0.1 0.3 ");
    assertEquals(array[0], 0.1f);
    assertEquals(array[1], 0.3f);
    assertEquals(array.length, 2);
    LabjackAdcFrame labjackAdcFrame = new LabjackAdcFrame(array);
    assertEquals(labjackAdcFrame.length(), 8);
  }
}
