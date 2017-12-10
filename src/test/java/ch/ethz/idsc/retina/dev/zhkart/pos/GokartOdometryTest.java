// code by jph
package ch.ethz.idsc.retina.dev.zhkart.pos;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class GokartOdometryTest extends TestCase {
  public void testSimple() {
    GokartOdometry gokartOdometry = new GokartOdometry();
    gokartOdometry.step(Tensors.fromString("{1[rad*s^-1], 2[rad*s^-1]}"));
    Tensor state = gokartOdometry.getState();
    System.out.println(state);
    // gokartOdometry.getEvent(rimoGetEvent);
  }
}
