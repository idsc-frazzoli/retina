// code by edo
package ch.ethz.idsc.retina.gui.gokart;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import idsc.BinaryBlob;
import lcm.lcm.LCM;

public enum ControllerInfoPublish {
  ;
  public static final String CHANNEL = "myChannel";

  public static void publish(double desPos, double currAngle) {
    BinaryBlob binaryBlob = new BinaryBlob();
    binaryBlob.data = new byte[16];
    binaryBlob.data_length = 16;
    ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    byteBuffer.putDouble(desPos);
    byteBuffer.putDouble(currAngle);
    LCM.getSingleton().publish(CHANNEL, binaryBlob);
  }
}
