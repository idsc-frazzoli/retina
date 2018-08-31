// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class CurvePurePursuitReverseTest extends TestCase {
  public void testSimple() throws Exception {
    CurvePurePursuitModule purePursuitModule = new CurvePurePursuitModule();
    purePursuitModule.first();
    assertTrue(purePursuitModule.isForward());
    purePursuitModule.rimoGetListener.getEvent(RimoGetEvents.create(1000, 1000));
    assertTrue(purePursuitModule.isForward());
    purePursuitModule.rimoGetListener.getEvent(RimoGetEvents.create(-1000, -1000));
    assertFalse(purePursuitModule.isForward());
    purePursuitModule.rimoGetListener.getEvent(RimoGetEvents.create(-10, -10));
    assertTrue(purePursuitModule.isForward());
    purePursuitModule.last();
  }

  public void testSpecificHLE() throws Exception {
    Tensor pose = Tensors.fromString("{50.0[m], 48.6[m], 0.0}");
    Optional<Scalar> optional = CurvePurePursuitModule.getRatio(pose, DubendorfCurve.HYPERLOOP_EIGHT, true);
    Scalar lookAhead = optional.get();
    assertTrue(Chop._08.close(lookAhead, RealScalar.of(0.0627054558616751)));
  }

  public void testSpecificHLE_R() throws Exception {
    Tensor pose = Tensors.fromString("{50.0[m], 48.6[m], 0.0}");
    Optional<Scalar> optional = CurvePurePursuitModule.getRatio(pose, DubendorfCurve.HYPERLOOP_EIGHT, false);
    Scalar lookAhead = optional.get();
    assertTrue(Chop._08.close(lookAhead, RealScalar.of(0.009685440563639316)));
  }

  public void testSpecificHLER() throws Exception {
    Tensor pose = Tensors.fromString("{50.0[m], 48.6[m], 3.1415926535897932385}");
    Optional<Scalar> optional = CurvePurePursuitModule.getRatio(pose, DubendorfCurve.HYPERLOOP_EIGHT_REVERSE, true);
    Scalar lookAhead = optional.get();
    assertTrue(Chop._08.close(lookAhead, RealScalar.of(-0.009685440563639316)));
  }

  public void testSpecificHLER_R() throws Exception {
    Tensor pose = Tensors.fromString("{50.0[m], 48.6[m], 3.1415926535897932385}");
    Optional<Scalar> optional = CurvePurePursuitModule.getRatio(pose, DubendorfCurve.HYPERLOOP_EIGHT_REVERSE, false);
    Scalar lookAhead = optional.get();
    assertTrue(Chop._08.close(lookAhead, RealScalar.of(-0.0627054558616751)));
  }
}
