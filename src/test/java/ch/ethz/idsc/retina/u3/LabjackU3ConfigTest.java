// code by jph
package ch.ethz.idsc.retina.u3;

import ch.ethz.idsc.tensor.io.UserName;
import junit.framework.TestCase;

public class LabjackU3ConfigTest extends TestCase {
  public void testSimple() {
    if (UserName.is("datahaki"))
      assertTrue(LabjackU3Config.INSTANCE.isFeasible());
  }
}
