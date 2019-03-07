// code by jph
package ch.ethz.idsc.gokart.offline.pose;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.lcm.OfflineLogListener;

public interface OfflinePoseEstimator extends OfflineLogListener {
  GokartPoseEvent getGokartPoseEvent();
}
