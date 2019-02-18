// code by jph
package ch.ethz.idsc.gokart.offline.pose;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.tensor.Scalar;

/** when gokart is stationary */
public class ConstantOfflinePoseEstimator implements OfflinePoseEstimator {
  private final GokartPoseEvent gokartPoseEvent;

  public ConstantOfflinePoseEstimator(GokartPoseEvent gokartPoseEvent) {
    this.gokartPoseEvent = gokartPoseEvent;
  }

  @Override
  public GokartPoseEvent getGokartPoseEvent() {
    return gokartPoseEvent;
  }

  @Override
  public void event(Scalar time, String channel, ByteBuffer byteBuffer) {
    // ---
  }
}
