// code by mg, jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.retina.util.math.SIDerived;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class WaypointPurePursuitModuleTest extends TestCase {
  public void testSetForward() {
    WaypointPurePursuitModule wppm = new WaypointPurePursuitModule();
    assertFalse(wppm.getRatio().isPresent());
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
    // System.out.println(ratio);
    assertTrue(Chop._12.close(ratio.get(), RealScalar.of(0.1081081081081081)));
    Optional<Scalar> heading = wppm.deriveHeading();
    // System.out.println(operational);
    assertTrue(heading.isPresent());
    // Scalar steer_heading = wppm.purePursuitSteer.getHeading();
    // System.out.println(heading);
    // System.out.println(steer_heading);
    assertTrue(Chop._12.close(heading.get(), Quantity.of(0.12794588215809746, SIDerived.RADIAN)));
  }

  public void testSetFar() {
    WaypointPurePursuitModule wppm = new WaypointPurePursuitModule();
    wppm.setLookAhead(Optional.of(Tensors.vector(1, 2)));
    Optional<Scalar> ratio = wppm.getRatio();
    assertTrue(ratio.isPresent());
    // System.out.println(ratio);
    // assertTrue(Chop._12.close(ratio.get(), RealScalar.of(0.1081081081081081)));
    Optional<Scalar> heading = wppm.deriveHeading();
    // System.out.println(operational);
    assertFalse(heading.isPresent());
    // Scalar heading = wppm.purePursuitSteer.getHeading();
    // // System.out.println(heading);
    // assertTrue(Chop._12.close(heading, Quantity.of(0.12794588215809746, SIDerived.RADIAN)));
  }

  public void testSetQuantity() {
    WaypointPurePursuitModule wppm = new WaypointPurePursuitModule();
    assertFalse(wppm.getRatio().isPresent());
    wppm.setLookAhead(Optional.of(Tensors.fromString("{3[m], 0.5[m]}")));
    Optional<Scalar> ratio = wppm.getRatio();
    assertTrue(ratio.isPresent());
    // System.out.println(ratio);
    assertTrue(Chop._12.close(ratio.get(), Quantity.of(0.1081081081081081, "m^-1")));
  }
}
