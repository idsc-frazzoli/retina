// code by jph
package ch.ethz.idsc.gokart.calib.power;

import java.io.IOException;

import ch.ethz.idsc.tensor.io.Serialization;
import junit.framework.TestCase;

public class MotorFunctionBaseTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    Serialization.copy(MotorFunctionV2.INSTANCE);
  }
}
