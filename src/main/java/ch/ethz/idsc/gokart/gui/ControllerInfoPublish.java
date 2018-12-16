// code by edo
package ch.ethz.idsc.gokart.gui;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.tensor.Scalar;
import idsc.BinaryBlob;
import lcm.lcm.LCM;

public enum ControllerInfoPublish {
  ;
  public static final String CHANNEL = "myChannel";

  public static void publish(Scalar desPos, Scalar currAngle) {
    BinaryBlob binaryBlob = new BinaryBlob();
    binaryBlob.data = new byte[16];
    binaryBlob.data_length = 16;
    ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.putDouble(SteerPutEvent.ENCODER.apply(desPos).number().doubleValue());
    byteBuffer.putDouble(SteerPutEvent.ENCODER.apply(currAngle).number().doubleValue());
    LCM.getSingleton().publish(CHANNEL, binaryBlob);
  }
}
