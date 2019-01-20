// code by jph
package ch.ethz.idsc.gokart.core.man;

import java.util.Objects;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import junit.framework.TestCase;

public class GetJoystickNameTest extends TestCase {
  public void testSimple() {
    GLFWErrorCallback.createPrint(System.err).set();
    // Initialize GLFW. Most GLFW functions will not work before doing this.
    if (!GLFW.glfwInit())
      throw new IllegalStateException("Unable to initialize GLFW");
    for (int index = GLFW.GLFW_JOYSTICK_1; index < GLFW.GLFW_JOYSTICK_LAST; ++index) {
      final String string = GLFW.glfwGetJoystickName(index);
      if (Objects.nonNull(string)) {
        System.err.println(string);
      }
    }
  }
}
