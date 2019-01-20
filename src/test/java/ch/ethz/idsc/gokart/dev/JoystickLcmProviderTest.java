// code by jph
package ch.ethz.idsc.gokart.dev;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.man.ManualConfig;
import ch.ethz.idsc.gokart.dev.u3.LabjackU3LcmModule;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.BinaryBlobPublisher;
import ch.ethz.idsc.retina.joystick.JoystickEncoder;
import ch.ethz.idsc.retina.joystick.JoystickType;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.joystick.ManualControlProvider;
import ch.ethz.idsc.retina.u3.LabjackAdcFrame;
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
    assertFalse(manualControlProvider.getManualControl().isPresent());
    manualControlProvider.start();
    assertFalse(manualControlProvider.getManualControl().isPresent());
    publishOne();
    Thread.sleep(40);
    Optional<ManualControlInterface> optional = manualControlProvider.getManualControl();
    assertTrue(optional.isPresent());
    ManualControlInterface gokartJoystickInterface = optional.get();
    assertFalse(gokartJoystickInterface.isAutonomousPressed());
    manualControlProvider.stop();
  }

  public void testAutonomous() {
    ManualControlProvider joystickLcmClient = ManualConfig.GLOBAL.createProvider();
    assertFalse(joystickLcmClient.getManualControl().isPresent());
    joystickLcmClient.start();
    assertFalse(joystickLcmClient.getManualControl().isPresent());
    LabjackU3LcmModule.accept(new LabjackAdcFrame(new float[] { 0, 0, 0, 5, 0 }));
    AllGunsBlazing.publishAutonomous();
    Optional<ManualControlInterface> optional = joystickLcmClient.getManualControl();
    assertTrue(optional.isPresent());
    ManualControlInterface gokartJoystickInterface = optional.get();
    assertTrue(gokartJoystickInterface.isAutonomousPressed());
    joystickLcmClient.stop();
  }
}
