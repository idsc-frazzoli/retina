// code by jph
package ch.ethz.idsc.gokart.core.pos;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.data.DataEventInterface;
import ch.ethz.idsc.retina.util.pose.PoseVelocityInterface;
import ch.ethz.idsc.tensor.Scalar;

/** an instance of {@link GokartPoseEvent} is immutable */
public interface GokartPoseEvent extends DataEventInterface, PoseVelocityInterface {
  /** .
   * ante 20190323: gokart pose event did not contain velocity and gyro
   * post 20190323: gokart pose event includes velocity and gyro
   * 
   * implementation is compatible with messages from all log files.
   * when velocity is not available, velocity and gyro are returned to be zero.
   * 
   * @return */
  static GokartPoseEvent of(ByteBuffer byteBuffer) {
    if (byteBuffer.remaining() == GokartPoseEventV2.LENGTH)
      return new GokartPoseEventV2(byteBuffer);
    return byteBuffer.remaining() == GokartPoseEventV1.LENGTH //
        ? new GokartPoseEventV1(byteBuffer)
        : new GokartPoseEventV0(byteBuffer);
  }

  /** @return value in the interval [0, 1] where
   * 0 represents no pose quality, and
   * 1 represents perfect pose quality */
  Scalar getQuality();

  /** @return whether velocity and gyro were set during the creation of the message */
  boolean hasVelocity();
}
