// code by jph
package ch.ethz.idsc.retina.lcm.joystick;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.joy.JoystickConfig;
import ch.ethz.idsc.gokart.dev.JoystickLcmProvider;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.joystick.JoystickEncoder;
import ch.ethz.idsc.retina.dev.joystick.JoystickType;
import ch.ethz.idsc.retina.dev.joystick.ManualControlProvider;
import ch.ethz.idsc.retina.lcm.BinaryBlobPublisher;
import junit.framework.TestCase;

public class JoystickLcmProviderTest extends TestCase {
  public static JoystickLcmProvider createJoystickLcmProvider() {
    return new JoystickLcmProvider(GokartLcmChannel.JOYSTICK, 0.2);
  }

  public static void publishOne() {
    BinaryBlobPublisher binaryBlobPublisher = new BinaryBlobPublisher(GokartLcmChannel.JOYSTICK);
    JoystickType joystickType = JoystickType.GENERIC_XBOX_PAD;
    byte[] data = new byte[joystickType.encodingSize()];
    FloatBuffer axes = FloatBuffer.wrap(new float[6]);
    ByteBuffer buttons = ByteBuffer.wrap(new byte[10]);
    ByteBuffer hats = ByteBuffer.wrap(new byte[1]);
    ByteBuffer byteBuffer = ByteBuffer.wrap(data);
    byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    JoystickEncoder.encode(joystickType, axes, buttons, hats, byteBuffer);
    binaryBlobPublisher.accept(data, data.length);
  }

  /** joystick with all zeros except autonomous button pressed */
  public static void publishAutonomous() {
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

  public void testSimple() throws Exception {
    ManualControlProvider manualControlProvider = createJoystickLcmProvider();
    assertFalse(manualControlProvider.getJoystick().isPresent());
    manualControlProvider.start();
    assertFalse(manualControlProvider.getJoystick().isPresent());
    publishOne();
    Thread.sleep(40);
    Optional<GokartJoystickInterface> optional = manualControlProvider.getJoystick();
    assertTrue(optional.isPresent());
    GokartJoystickInterface gokartJoystickInterface = optional.get();
    assertFalse(gokartJoystickInterface.isAutonomousPressed());
    manualControlProvider.stop();
  }

  public void testAutonomous() {
    ManualControlProvider joystickLcmClient = JoystickConfig.GLOBAL.createProvider();
    assertFalse(joystickLcmClient.getJoystick().isPresent());
    joystickLcmClient.start();
    assertFalse(joystickLcmClient.getJoystick().isPresent());
    publishAutonomous();
    Optional<GokartJoystickInterface> optional = joystickLcmClient.getJoystick();
    assertTrue(optional.isPresent());
    GokartJoystickInterface gokartJoystickInterface = optional.get();
    assertTrue(gokartJoystickInterface.isAutonomousPressed());
    // System.out.println(gokartJoystickInterface.getAheadAverage());
    joystickLcmClient.stop();
  }
}
