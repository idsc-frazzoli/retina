// code by em
package ch.ethz.idsc.gokart.lcm.led;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.lcm.ArrayFloatBlob;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.VectorQ;
import idsc.BinaryBlob;
import lcm.lcm.LCM;

public class LEDLcm {
  /** encode uses the method ArrayFloatBlob::encode to turn the received message
   * ((in this case, a vector of integers int[],
   * that will be characterized by steering position/torque in the future)
   * into a BinaryBlob */
  public static BinaryBlob encode(int[] sp) {
    Tensor inTensor=Tensors.vectorInt(sp);
    return ArrayFloatBlob.encode(inTensor);
  }

  /** publish sends the BinaryBlob through the "led.color" channel
   * using LCM.getSingleton()::publish. */
  public static void publish(String channel, int[] sp) {
    LCM.getSingleton().publish(channel, encode(sp));
  }

  /** decode gets back int[] from a ByteBuffer using ArrayFloatBlob::decode. */
  public static int[] decode(ByteBuffer byteBuffer) {
    Tensor decoded = VectorQ.require(ArrayFloatBlob.decode(byteBuffer));
    return decoded.stream().mapToInt(t -> t.Get().number().intValue()).toArray();
  }
}
