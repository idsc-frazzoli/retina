// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import java.io.IOException;

import ch.ethz.idsc.owl.car.core.TwdOdometry;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class RimoTwdOdometryTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    TwdOdometry twdOdometry = Serialization.copy(RimoTwdOdometry.INSTANCE);
    Tensor angularRate_Y_pair = Tensors.fromString("{3[s^-1], 4[s^-1]}");
    Scalar tangentSpeed = twdOdometry.tangentSpeed(angularRate_Y_pair);
    Chop._01.requireClose(tangentSpeed, Quantity.of(0.42, SI.VELOCITY));
    Scalar turningRate = twdOdometry.turningRate(angularRate_Y_pair);
    Chop._01.requireClose(turningRate, Quantity.of(0.11111, SI.PER_SECOND));
  }
}
