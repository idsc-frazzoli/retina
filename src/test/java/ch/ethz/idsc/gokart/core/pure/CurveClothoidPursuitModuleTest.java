// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.OvalTrack;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvents;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class CurveClothoidPursuitModuleTest extends TestCase {
  public void testSimple() {
    CurveClothoidPursuitModule curveClothoidPursuitModule = new CurveClothoidPursuitModule(ClothoidPursuitConfig.GLOBAL);
    curveClothoidPursuitModule.launch();
    curveClothoidPursuitModule.gokartPoseEvent = GokartPoseEvents.motionlessUninitialized();
    {
      Optional<Scalar> ratio = curveClothoidPursuitModule.getRatio(OvalTrack.SE2.get(3));
      assertFalse(ratio.isPresent());
    }
    curveClothoidPursuitModule.setCurve(Optional.of(OvalTrack.SE2));
    {
      Optional<Scalar> ratio = curveClothoidPursuitModule.getRatio(OvalTrack.SE2.get(3));
      assertTrue(ratio.isPresent());
      Clips.interval(-0.03, -0.01).requireInside(Magnitude.PER_METER.apply(ratio.get()));
    }
    curveClothoidPursuitModule.terminate();
  }
}
