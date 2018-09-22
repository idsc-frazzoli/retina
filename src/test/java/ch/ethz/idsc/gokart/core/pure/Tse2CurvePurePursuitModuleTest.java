// code by jph
package ch.ethz.idsc.gokart.core.pure;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class Tse2CurvePurePursuitModuleTest extends TestCase {
  public void testSimple() {
    Tse2CurvePurePursuitModule tse2CurvePurePursuitModule = //
        new Tse2CurvePurePursuitModule(PursuitConfig.GLOBAL);
    tse2CurvePurePursuitModule.gokartPoseEvent = GokartPoseEvents.getPoseEvent(Tensors.fromString("{1[m],2[m],3}"), RealScalar.of(.8));
    Scalar scalar = tse2CurvePurePursuitModule.getSpeedMultiplier();
    assertEquals(scalar, RealScalar.ZERO);
  }
}
