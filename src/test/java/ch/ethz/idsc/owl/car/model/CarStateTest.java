// code by jph
package ch.ethz.idsc.owl.car.model;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Range;
import junit.framework.TestCase;

public class CarStateTest extends TestCase {
  public void testSimple() {
    Tensor x = Range.of(3, 3 + 10);
    CarState cs = new CarState(x);
    assertEquals(x, cs.asVector());
  }

  public void testX0_Demo1() {
    // CarState carState =
    CarStatic.x0_demo2();
    // carState.asVector().forEach(System.out::println);
  }
}
