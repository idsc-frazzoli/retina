// code by jph
package ch.ethz.idsc.demo.mg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

public class ListRemoveTest extends TestCase {
  public void testSimple() {
    List<Integer> list = new ArrayList<>();
    list.add(3);
    list.add(10);
    list.add(6);
    list.remove(1);
    assertEquals(list, Arrays.asList(3, 6));
  }
}
