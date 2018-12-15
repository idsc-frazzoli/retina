// code by jph
package ch.ethz.idsc.gokart.dev;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.dev.joystick.JoystickEncoder;
import ch.ethz.idsc.retina.dev.joystick.JoystickType;
import ch.ethz.idsc.retina.dev.u3.LabjackAdcFrame;
import ch.ethz.idsc.retina.lcm.BinaryBlobPublisher;

public enum AllGunsBlazing {
  ;
  /** joystick with all zeros except autonomous button pressed */
  public static void publishAutonomous() {
    LabjackU3LcmModule.accept(new LabjackAdcFrame(new float[] { 0, 0, 0, 5, 0 }));
    // ---
    BinaryBlobPublisher binaryBlobPublisher = new BinaryBlobPublisher(GokartLcmChannel.JOYSTICK);
    JoystickType joystickType = JoystickType.GENERIC_XBOX_PAD;
    byte[] data = new byte[joystickType.encodingSize()];
    FloatBuffer axes = FloatBuffer.wrap(new float[6]);
    byte[] b_data = new byte[10];
    b_data[0] = 1;
    ByteBuffer buttons = ByteBuffer.wrap(b_data);
    ByteBuffer hats = ByteBuffer.wrap(new byte[1]);
    ByteBuffer byteBuffer = ByteBuffer.wrap(data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    JoystickEncoder.encode(joystickType, axes, buttons, hats, byteBuffer);
    binaryBlobPublisher.accept(data, data.length);
    try {
      Thread.sleep(30);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
