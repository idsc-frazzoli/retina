// code by mg
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class WaypointPurePursuitModuleTest extends TestCase {
  public void testSetForward() {
    WaypointPurePursuitModule wppm = new WaypointPurePursuitModule();
    wppm.setLookAhead(Optional.of(Tensors.vector(3, 0)));
    Optional<Scalar> ratio = wppm.getRatio();
    assertTrue(ratio.isPresent());
    assertTrue(Scalars.isZero(ratio.get()));
  }

  public void testSetRight() {
    WaypointPurePursuitModule wppm = new WaypointPurePursuitModule();
    wppm.setLookAhead(Optional.of(Tensors.vector(3, 0.5)));
    Optional<Scalar> ratio = wppm.getRatio();
    assertTrue(ratio.isPresent());
    System.out.println(ratio);
    assertTrue(Chop._12.close(ratio.get(), RealScalar.of(0.1081081081081081)));
  }
}
