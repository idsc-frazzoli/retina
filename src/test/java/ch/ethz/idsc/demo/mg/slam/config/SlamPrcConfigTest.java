// code by jph
package ch.ethz.idsc.demo.mg.slam.config;

import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class SlamPrcConfigTest extends TestCase {
  public void testSimple() {
    for (EventCamera eventCamera : EventCamera.values())
      Clips.unit().requireInside(eventCamera.slamPrcConfig.alphaCurvature);
  }
}
