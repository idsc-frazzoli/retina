// code by jph
package ch.ethz.idsc.gokart.dev;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.joy.ManualConfig;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.joystick.JoystickEncoder;
import ch.ethz.idsc.retina.dev.joystick.JoystickType;
import ch.ethz.idsc.retina.dev.joystick.ManualControlProvider;
import ch.ethz.idsc.retina.dev.u3.LabjackAdcFrame;
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
    ManualControlProvider joystickLcmClient = ManualConfig.GLOBAL.createProvider();
    assertFalse(joystickLcmClient.getJoystick().isPresent());
    joystickLcmClient.start();
    assertFalse(joystickLcmClient.getJoystick().isPresent());
    LabjackU3LcmModule.accept(new LabjackAdcFrame(new float[] { 0, 0, 0, 5, 0 }));
    AllGunsBlazing.publishAutonomous();
    Optional<GokartJoystickInterface> optional = joystickLcmClient.getJoystick();
    assertTrue(optional.isPresent());
    GokartJoystickInterface gokartJoystickInterface = optional.get();
    assertTrue(gokartJoystickInterface.isAutonomousPressed());
    // System.out.println(gokartJoystickInterface.getAheadAverage());
    joystickLcmClient.stop();
  }
}
