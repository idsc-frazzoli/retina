// code by jph
package ch.ethz.idsc.gokart.core.pos;

import ch.ethz.idsc.gokart.core.fuse.DavisImuTracker;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class GokartGyroPoseOdometryTest extends TestCase {
  public void testSimple() {
    Tensor state = Tensors.fromString("{1[m],2[m],3}");
    GokartPoseOdometry gokartPoseOdometry = GokartGyroPoseOdometry.create(state);
    assertEquals(Magnitude.SECOND.toDouble(gokartPoseOdometry.dt), 1 / 250.);
    assertEquals(gokartPoseOdometry.state, state);
    DavisImuTracker.INSTANCE.setGyroZ(Quantity.of(0, SI.PER_SECOND));
    gokartPoseOdometry.getEvent(RimoGetEvents.create(1000, -1000));
    assertEquals(gokartPoseOdometry.state, state);
    gokartPoseOdometry.getEvent(RimoGetEvents.create(100, 100));
    assertTrue(Chop._10.close(gokartPoseOdometry.state, //
        Tensors.fromString("{0.9992080060027196[m], 2.000112896006448[m], 3}")));
    DavisImuTracker.INSTANCE.setGyroZ(Quantity.of(2, SI.PER_SECOND));
    gokartPoseOdometry.getEvent(RimoGetEvents.create(100, 100));
    assertTrue(Chop._10.close(gokartPoseOdometry.state, //
        Tensors.fromString("{0.9984155688717309[m], 2.000222622849582[m], 3.008}")));
  }
}
