// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.DubendorfCurve;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class CurveClothoidPursuitModuleTest extends TestCase {
  public void testSimple() {
    CurveClothoidPursuitModule curveClothoidPursuitModule = new CurveClothoidPursuitModule(ClothoidPursuitConfig.GLOBAL);
    curveClothoidPursuitModule.launch();
    {
      Optional<Scalar> ratio = curveClothoidPursuitModule.getRatio(DubendorfCurve.TRACK_OVAL_SE2.get(3));
      assertFalse(ratio.isPresent());
    }
    curveClothoidPursuitModule.setCurve(Optional.of(DubendorfCurve.TRACK_OVAL_SE2));
    {
      Optional<Scalar> ratio = curveClothoidPursuitModule.getRatio(DubendorfCurve.TRACK_OVAL_SE2.get(3));
      assertTrue(ratio.isPresent());
      Clips.interval(-0.03, -0.01).requireInside(Magnitude.PER_METER.apply(ratio.get()));
    }
    curveClothoidPursuitModule.terminate();
  }
}
