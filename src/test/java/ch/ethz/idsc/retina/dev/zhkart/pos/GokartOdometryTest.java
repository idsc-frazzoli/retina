// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pos;

import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class GokartOdometryTest extends TestCase {
  public void testSimple() {
    GokartOdometry gokartOdometry = GokartOdometry.create();
    gokartOdometry.step(Tensors.fromString("{5[rad*s^-1], 10[rad*s^-1]}"));
    Tensor state = gokartOdometry.getState();
    assertTrue(Sign.isPositive(state.Get(0)));
    assertTrue(Sign.isPositive(state.Get(1)));
    assertTrue(Sign.isPositive(state.Get(2)));
    // System.out.println(state);
    // gokartOdometry.getEvent(rimoGetEvent);
  }

  public void testEffective() {
    GokartOdometry gokartOdometry = GokartOdometry.create();
    Flow flow = gokartOdometry.singleton( //
        Quantity.of(3, "m*s^-1"), Quantity.of(5, "m*s^-1"), Quantity.of(0.3, "m*rad^-1"));
    Tensor u = flow.getU();
    assertEquals(u.Get(0), Quantity.of(4, "m*s^-1"));
  }
}
