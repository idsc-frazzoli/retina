// code by gjoel
package ch.ethz.idsc.gokart.lcm.mod;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Optional;

import ch.ethz.idsc.gokart.lcm.ArrayFloatBlob;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.ScalarQ;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.alg.Last;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.qty.Boole;
import idsc.BinaryBlob;
import lcm.lcm.LCM;

public enum PursuitPlanLcm {
  ;
  /** @param channel
   * @param pose {x[m], y[m], angle}
   * @param lookAhead {x[m], y[m], angle}
   * @param isForward whether vehicle drives forward */
  public static void publish(String channel, Tensor pose, Tensor lookAhead, boolean isForward) {
    LCM.getSingleton().publish(channel, encode(isForward, pose, lookAhead));
  }

  /** @param tensors of the form {x[m], y[m], angle}
   * @param isForward whether vehicle drives forward
   * @return */
  public static BinaryBlob encode(boolean isForward, Tensor... tensors) {
    return ArrayFloatBlob.encode(Arrays.stream(tensors).map(PoseHelper::toUnitless).reduce(Join::of).get().append(Boole.of(isForward)));
  }

  /** @param byteBuffer
   * @return tensor with rows of the form {x[m], y[m], angle} or Boole*/
  public static Tensor decode(ByteBuffer byteBuffer) {
    Tensor decoded = ArrayFloatBlob.decode(byteBuffer);
    if (VectorQ.of(decoded)) {
      Tensor tensor = Tensors.empty();
      for (int i = 0; i < decoded.length(); i += 3)
        tensor.append(PoseHelper.attachUnits(decoded.extract(i, i + 3)));
      return tensor.append(Last.of(decoded));
    }
    return Tensor.of(decoded.stream().map(PoseHelper::attachUnits));
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

  /** @param byteBuffer
   * @return whether vehicle drives forward */
  public static Optional<Boolean> decodeIsForward(ByteBuffer byteBuffer) {
    return decodeIsForward(decode(byteBuffer));
  }

  /** @param decoded Tensor
   * @return whether vehicle drives forward */
  /* package */ static Optional<Boolean> decodeIsForward(Tensor decoded) {
    Tensor last = Last.of(decoded);
    if (ScalarQ.of(last))
      return Optional.of(last.equals(RealScalar.ONE));
    return Optional.empty();
  }
}
