// code by mg
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class WaypointPurePursuitModuleTest extends TestCase {
  public void testSimple() {
    WaypointPurePursuitModule wppm = new WaypointPurePursuitModule();
    Optional<Tensor> lookAhead = Optional.of(Tensors.of(RealScalar.of(23), RealScalar.of(27)));
    wppm.setLookAhead(lookAhead);
    Tensor pose = Tensors.of(Quantity.of(20, SI.METER), Quantity.of(25, SI.METER), Quantity.of(0, SI.ONE));
    Scalar ratio = wppm.getRatio(pose, true).get();
    assertTrue(Chop._12.close(ratio, RealScalar.of(0.30769230769230765)));
  }

  public void testEmptyInvoke() {
    WaypointPurePursuitModule wppm = new WaypointPurePursuitModule();
    wppm.getRatio(Tensors.vector(0, 0, 0), true);
  }

  public void testSetForward() {
    WaypointPurePursuitModule wppm = new WaypointPurePursuitModule();
    wppm.setLookAhead(Optional.of(Tensors.vector(11, 0)));
    Optional<Scalar> ratio = wppm.getRatio(Tensors.fromString("{10[m],0[m],0}"), true);
    assertTrue(ratio.isPresent());
    assertTrue(Scalars.isZero(ratio.get()));
  }

  public void testSetRight() {
    WaypointPurePursuitModule wppm = new WaypointPurePursuitModule();
    wppm.setLookAhead(Optional.of(Tensors.vector(15, 2)));
    Optional<Scalar> ratio = wppm.getRatio(Tensors.fromString("{10[m],1[m],0}"), true);
    assertTrue(ratio.isPresent());
    assertTrue(Chop._12.close(ratio.get(), RealScalar.of(0.07692307692307693)));
  }
}
