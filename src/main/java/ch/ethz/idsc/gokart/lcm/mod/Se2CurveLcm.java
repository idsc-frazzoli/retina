// code by jph
package ch.ethz.idsc.gokart.lcm.mod;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.lcm.ArrayFloatBlob;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.tensor.Tensor;
import idsc.BinaryBlob;
import lcm.lcm.LCM;

public enum Se2CurveLcm {
  ;
  /** @param channel
   * @param tensor with rows of the form {x[m], y[m], angle} */
  public static void publish(String channel, Tensor tensor) {
    LCM.getSingleton().publish(channel, encode(tensor));
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
}
