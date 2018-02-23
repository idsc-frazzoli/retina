// code by jph
package ch.ethz.idsc.gokart.lcm.mod;

import junit.framework.TestCase;

public class AutoboxLcmServerModuleTest extends TestCase {
  public void testFirstLast() throws Exception {
    AutoboxLcmServerModule am = new AutoboxLcmServerModule();
    am.first();
    am.last();
  }
}
