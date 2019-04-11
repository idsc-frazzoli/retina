// code by jph
package ch.ethz.idsc.gokart.core.pos;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.gokart.offline.pose.LogPosePostInject;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public enum GokartPoseEvents {
  ;
  private static final GokartPoseEvent MOTIONLESS_0 = create( //
      GokartPoseLocal.INSTANCE.getPose(), //
      RealScalar.ZERO, //
      GokartPoseEventV1.VELOCITY_ZERO, //
      GokartPoseEventV1.GYROZ_ZERO);

  /** @return motionless with pose quality == 0, instance of {@link GokartPoseEventV2} */
  public static GokartPoseEvent motionlessUninitialized() {
    return MOTIONLESS_0;
  }

  /** @param pose {x[m], y[m], alpha}
   * @param quality in the interval [0, 1]
   * @param velocityXY {vx[m*s^-1], vy[m*s^-1]}
   * @param gyroZ with unit "s^-1"
   * @return instances of {@link GokartPoseEventV2} */
  public static GokartPoseEvent create(Tensor pose, Scalar quality, Tensor velocityXY, Scalar gyroZ) {
    byte[] array = new byte[GokartPoseEventV2.LENGTH];
    ByteBuffer byteBuffer = ByteBuffer.wrap(array);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.putDouble(Magnitude.METER.toDouble(pose.Get(0)));
    byteBuffer.putDouble(Magnitude.METER.toDouble(pose.Get(1)));
    byteBuffer.putDouble(Magnitude.ONE.toDouble(pose.Get(2)));
    byteBuffer.putFloat(Magnitude.ONE.toFloat(quality));
    // ---
    byteBuffer.putFloat(Magnitude.VELOCITY.toFloat(velocityXY.Get(0)));
    byteBuffer.putFloat(Magnitude.VELOCITY.toFloat(velocityXY.Get(1)));
    // ---
    byteBuffer.putFloat(Magnitude.PER_SECOND.toFloat(gyroZ));
    // ---
    byteBuffer.flip();
    return new GokartPoseEventV2(byteBuffer);
  }

  /** @param pose {x[m], y[m], alpha}
   * @param quality in the interval [0, 1] */
  public static GokartPoseEvent create(Tensor pose, Scalar quality) {
    return create(pose, quality, GokartPoseEventV1.VELOCITY_ZERO, GokartPoseEventV1.GYROZ_ZERO);
  }

  /***************************************************/
  /** Hint: do not use function during live operation!
   * 
   * The function is intended for log file event injection,
   * for instance in {@link LogPosePostInject}.
   * 
   * @param pose {x[m], y[m], alpha}
   * @param quality in the interval [0, 1]
   * @return pose event without velocity */
  public static GokartPoseEvent offlineV1(Tensor pose, Scalar quality) {
    byte[] array = new byte[GokartPoseEventV2.LENGTH];
    ByteBuffer byteBuffer = ByteBuffer.wrap(array);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.putDouble(Magnitude.METER.toDouble(pose.Get(0)));
    byteBuffer.putDouble(Magnitude.METER.toDouble(pose.Get(1)));
    byteBuffer.putDouble(Magnitude.ONE.toDouble(pose.Get(2)));
    byteBuffer.putFloat(Magnitude.ONE.toFloat(quality));
    // ---
    byteBuffer.flip();
    return new GokartPoseEventV1(byteBuffer);
  }
}
