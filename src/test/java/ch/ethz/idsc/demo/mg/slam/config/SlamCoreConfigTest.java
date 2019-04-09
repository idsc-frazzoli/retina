// code by jph
package ch.ethz.idsc.demo.mg.slam.config;

import ch.ethz.idsc.demo.mg.slam.SlamAlgoConfig;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class SlamCoreConfigTest extends TestCase {
  public void testSimple() {
    for (EventCamera eventCamera : EventCamera.values()) {
      Tensor high = eventCamera.slamCoreConfig.cornerHigh();
      Clip clip = Clips.interval(Quantity.of(60, SI.METER), Quantity.of(80, SI.METER));
      assertTrue(clip.isInside(high.Get(0)));
      assertTrue(clip.isInside(high.Get(1)));
    }
  }

  public void testSlamAlgo() {
    for (EventCamera eventCamera : EventCamera.values()) {
      SlamAlgoConfig slamAlgoConfig = eventCamera.slamCoreConfig.slamAlgoConfig;
      assertNotNull(slamAlgoConfig);
    }
  }
}
