// code by jph
package ch.ethz.idsc.demo.jph.sys;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.retina.lcm.OfflineLogListener;

interface OfflinePoseEstimator extends OfflineLogListener {
  GokartPoseEvent getGokartPoseEvent();
}
