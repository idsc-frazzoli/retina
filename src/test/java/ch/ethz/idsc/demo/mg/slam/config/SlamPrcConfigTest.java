// code by jph
package ch.ethz.idsc.demo.mg.slam.config;

import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class SlamPrcConfigTest extends TestCase {
  public void testSimple() {
    for (EventCamera eventCamera : EventCamera.values())
      Clip.unit().requireInside(eventCamera.slamPrcConfig.alphaCurvature);
  }
}
