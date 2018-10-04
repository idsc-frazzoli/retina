// code by jph
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.demo.mg.slam.config.SlamCoreConfig;
import ch.ethz.idsc.demo.mg.slam.config.SlamDvsConfig;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import junit.framework.TestCase;

public class SlamCoreConfigTest extends TestCase {
  public void testSimple() {
    SlamDvsConfig.cameraType = "sEye";
    Tensor high = SlamCoreConfig.GLOBAL.cornerHigh();
    Clip clip = Clip.function(Quantity.of(60, SI.METER), Quantity.of(80, SI.METER));
    assertTrue(clip.isInside(high.Get(0)));
    assertTrue(clip.isInside(high.Get(1)));
  }

  public void testSlamAlgo() {
    SlamDvsConfig.cameraType = "davis";
    SlamAlgoConfig slamAlgoConfig = SlamCoreConfig.GLOBAL.slamAlgoConfig;
    assertNotNull(slamAlgoConfig);
  }
}
