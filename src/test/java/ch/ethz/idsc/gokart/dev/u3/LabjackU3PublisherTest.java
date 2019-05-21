// code by jph
package ch.ethz.idsc.gokart.dev.u3;

import ch.ethz.idsc.retina.u3.LabjackAdcFrame;
import junit.framework.TestCase;

public class LabjackU3PublisherTest extends TestCase {
  public void testStatic() throws Exception {
    LabjackU3Publisher.accept(new LabjackAdcFrame(new float[5]));
  }
}
