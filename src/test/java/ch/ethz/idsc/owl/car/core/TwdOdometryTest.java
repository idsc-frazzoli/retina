// code by jph
package ch.ethz.idsc.owl.car.core;

import ch.ethz.idsc.gokart.calib.steer.RimoAxleConfiguration;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class TwdOdometryTest extends TestCase {
  public void testSimple() {
    TwdOdometry twdOdometry = new TwdOdometry(RimoAxleConfiguration.rear());
    Scalar tangentSpeed = twdOdometry.tangentSpeed(Tensors.fromString("{1[s^-1], 1[s^-1]}"));
    Chop._12.requireClose(tangentSpeed, Quantity.of(0.12, SI.VELOCITY));
    Scalar turningRate = twdOdometry.turningRate(Tensors.fromString("{1.2[s^-1], 3[s^-1]}"));
    Chop._12.requireClose(turningRate, Quantity.of(0.2, SI.PER_SECOND));
    Tensor velocity = twdOdometry.velocity(Tensors.fromString("{1.2[s^-1], 3[s^-1]}"));
    Chop._12.requireClose(velocity, Tensors.fromString("{0.252[m*s^-1], 0.0[m*s^-1], 0.2[s^-1]}"));
  }
}
