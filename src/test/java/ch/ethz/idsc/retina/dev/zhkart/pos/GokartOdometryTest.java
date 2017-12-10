// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pos;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Sign;
import junit.framework.TestCase;

public class GokartOdometryTest extends TestCase {
  public void testSimple() {
    GokartOdometry gokartOdometry = new GokartOdometry();
    gokartOdometry.step(Tensors.fromString("{5[rad*s^-1], 10[rad*s^-1]}"));
    Tensor state = gokartOdometry.getState();
    assertTrue(Sign.isPositive(state.Get(0)));
    assertTrue(Sign.isPositive(state.Get(1)));
    assertTrue(Sign.isPositive(state.Get(2)));
    // System.out.println(state);
    // gokartOdometry.getEvent(rimoGetEvent);
  }
}
