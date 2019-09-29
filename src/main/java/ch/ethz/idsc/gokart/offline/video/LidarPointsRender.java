// code by jph
package ch.ethz.idsc.gokart.offline.video;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.owl.gui.RenderInterface;

/* package */ interface LidarPointsRender extends RenderInterface, GokartPoseListener {
  void lasers(ByteBuffer byteBuffer);
}
