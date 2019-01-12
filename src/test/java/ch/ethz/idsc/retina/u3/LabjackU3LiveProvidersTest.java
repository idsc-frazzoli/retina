// code by jph
package ch.ethz.idsc.retina.u3;

import ch.ethz.idsc.retina.util.StartAndStoppable;
import junit.framework.TestCase;

public class LabjackU3LiveProvidersTest extends TestCase {
  public void testSimple() {
    StartAndStoppable startAndStoppable = LabjackU3LiveProviders.create(s -> {
      // ---
    });
    assertNotNull(startAndStoppable);
  }
}
