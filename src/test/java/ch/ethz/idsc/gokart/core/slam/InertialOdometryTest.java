// code by jph
package ch.ethz.idsc.gokart.core.slam;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.HilbertMatrix;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class InertialOdometryTest extends TestCase {
  public void testInitial() {
    InertialOdometry inertialOdometry = new InertialOdometry();
    assertEquals(inertialOdometry.getPose(), Tensors.fromString("{0[m], 0[m], 0}"));
    assertEquals(inertialOdometry.getVelocity(), Tensors.fromString("{0[m*s^-1], 0[m*s^-1], 0[s^-1]}"));
    inertialOdometry.resetPose(PoseHelper.attachUnits(Tensors.vector(1, 2, 3)));
    assertEquals(inertialOdometry.getPose(), Tensors.fromString("{1[m], 2[m], 3}"));
    inertialOdometry.resetVelocity();
    assertEquals(inertialOdometry.getVelocity(), Tensors.fromString("{0[m*s^-1], 0[m*s^-1], 0[s^-1]}"));
  }

  public void testIntegrate() {
    InertialOdometry inertialOdometry = new InertialOdometry();
    inertialOdometry.resetPose(PoseHelper.attachUnits(Tensors.vector(1, 0, 0)));
    inertialOdometry.integrateImu( //
        Tensors.fromString("{0.3[m*s^-2], 0.1[m*s^-2]}"), Quantity.of(0.3, SI.PER_SECOND), Quantity.of(0.1, SI.SECOND));
    Chop._10.requireClose(inertialOdometry.getPose(), //
        Tensors.fromString("{1.002984551145215818[m], 0.0010448466318511013[m], 0.03}"));
    Tensor velocity = inertialOdometry.getVelocity();
    Chop._10.requireClose(velocity, Tensors.fromString("{0.03[m*s^-1], 0.01[m*s^-1], 0.3[s^-1]}"));
  }

  public void testSetPoseFail() {
    InertialOdometry inertialOdometry = new InertialOdometry();
    try {
      inertialOdometry.resetPose(Tensors.vector(1, 0, 0));
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      inertialOdometry.resetPose(HilbertMatrix.of(3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
