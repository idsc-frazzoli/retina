// code by jph
package ch.ethz.idsc.gokart.core.pos;

import ch.ethz.idsc.gokart.core.GetListener;

/** receives rimo get events from left and right wheel */
@FunctionalInterface
public interface GokartPoseListener extends GetListener<GokartPoseEvent> {
  // ---
}
