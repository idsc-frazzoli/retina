// code by jph
package ch.ethz.idsc.gokart.gui;

import java.util.ArrayList;

import ch.ethz.idsc.gokart.lcm.mod.PlannerPublish;
import junit.framework.TestCase;

public class GokartLcmChannelTest extends TestCase {
  public void testEmpty() {
    PlannerPublish.publishTrajectory(GokartLcmChannel.TRAJECTORY_XYAT_STATETIME, new ArrayList<>());
  }
}
