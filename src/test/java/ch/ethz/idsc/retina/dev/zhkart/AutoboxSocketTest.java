// code by jph
package ch.ethz.idsc.retina.dev.zhkart;

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

public class AutoboxSocketTest extends TestCase {
  public void testSimple() {
    Object object = new LinkedList<Integer>();
    List<String> list = (List<String>) object;
  }
}
