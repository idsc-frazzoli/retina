// code by jph
package ch.ethz.idsc.gokart.core.map;

import ch.ethz.idsc.retina.util.pose.PoseHelper;
import junit.framework.TestCase;

public class OccupancyConfigTest extends TestCase {
  public void testSimple() {
    PoseHelper.require(OccupancyConfig.GLOBAL.origin);
  }
}
