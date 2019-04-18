// code by jph
package ch.ethz.idsc.gokart.core.fuse;

import ch.ethz.idsc.tensor.alg.VectorQ;
import junit.framework.TestCase;

public class Urg04lxConfigTest extends TestCase {
  public void testSimple() {
    VectorQ.ofLength(Urg04lxConfig.GLOBAL.urg04lx, 3);
  }
}
