// code by jph
package ch.ethz.idsc.gokart.core.man;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import ch.ethz.idsc.gokart.lcm.BinaryBlobPublisher;
import ch.ethz.idsc.retina.joystick.JoystickEncoder;
import ch.ethz.idsc.retina.joystick.JoystickType;
import ch.ethz.idsc.retina.util.StartAndStoppable;

public class JoystickLcmServer implements StartAndStoppable {
  private static String formatName(String string) {
    return string.replace(' ', '_').replaceAll("\\W", "").toUpperCase();
  }

  // ---
  private final JoystickType joystickType;
  private final int period;
  private int joystick = -1;
  private BinaryBlobPublisher publisher;
  private Timer timer;

  public JoystickLcmServer(JoystickType joystickType, String channel, int period) {
    this.joystickType = joystickType;
    this.period = period;
    GLFWErrorCallback.createPrint(System.err).set();
    // Initialize GLFW. Most GLFW functions will not work before doing this.
    if (!GLFW.glfwInit())
      throw new IllegalStateException("Unable to initialize GLFW");
    for (int index = GLFW.GLFW_JOYSTICK_1; index < GLFW.GLFW_JOYSTICK_LAST; ++index) {
      final String string = GLFW.glfwGetJoystickName(index);
      System.out.println(string);
      if (Objects.nonNull(string)) {
        final String generic = formatName(string);
        if (generic.equals(joystickType.name())) {
          System.out.println("found joystick " + joystickType);
          joystick = index;
          publisher = new BinaryBlobPublisher(channel);
          break;
        }
      }
    }
    if (Objects.isNull(publisher))
      throw new RuntimeException("joystick not found: " + joystickType);
  }

  @Override
  public void start() {
    stop();
    timer = new Timer();
    TimerTask timerTask = new TimerTask() {
      private final byte[] data = new byte[joystickType.encodingSize()];

      @Override
      public void run() {
        FloatBuffer axes = GLFW.glfwGetJoystickAxes(joystick);
        ByteBuffer buttons = GLFW.glfwGetJoystickButtons(joystick);
        ByteBuffer hats = GLFW.glfwGetJoystickHats(joystick);
        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        JoystickEncoder.encode(joystickType, axes, buttons, hats, byteBuffer);
        publisher.accept(data, byteBuffer.limit());
      }
    };
    timer.schedule(timerTask, 100, period);
  }

  @Override
  public void stop() {
    if (Objects.nonNull(timer)) {
      timer.cancel();
      timer = null;
    }
  }
}
