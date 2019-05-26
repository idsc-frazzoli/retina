// code by gjoel
package ch.ethz.idsc.gokart.lcm.mod;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.lcm.ArrayFloatBlob;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import idsc.BinaryBlob;
import lcm.lcm.LCM;

public enum PursuitPlanLcm {
  ;
  /** @param channel
   * @param pose {x[m], y[m], angle}
   * @param lookAhead {x[m], y[m], angle} */
  public static void publish(String channel, Tensor pose, Tensor lookAhead) {
    LCM.getSingleton().publish(channel, encode(Tensors.of(pose, lookAhead)));
  }

  /** @param tensor with rows of the form {x[m], y[m], angle}
   * @return */
  public static BinaryBlob encode(Tensor tensor) {
    return ArrayFloatBlob.encode(Tensor.of(tensor.stream().map(PoseHelper::toUnitless)));
  }

  /** @param byteBuffer
   * @return tensor with rows of the form {x[m], y[m], angle} */
  public static Tensor decode(ByteBuffer byteBuffer) {
    return Tensor.of(ArrayFloatBlob.decode(byteBuffer).stream().map(PoseHelper::attachUnits));
  }

  /** @param byteBuffer
   * @return pose {x[m], y[m], angle} */
  public static Tensor decodePose(ByteBuffer byteBuffer) {
    return decode(byteBuffer).get(0);
  }

  /** @param byteBuffer
   * @return lookAhead {x[m], y[m], angle} */
  public static Tensor decodeLookAhead(ByteBuffer byteBuffer) {
    return decode(byteBuffer).get(1);
  }
}
