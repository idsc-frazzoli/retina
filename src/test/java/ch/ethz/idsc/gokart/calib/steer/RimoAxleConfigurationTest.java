// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import java.io.IOException;

import ch.ethz.idsc.owl.car.core.AxleConfiguration;
import ch.ethz.idsc.owl.car.core.TwdOdometry;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class RimoAxleConfigurationTest extends TestCase {
  public void testSimple() {
    AxleConfiguration axleConfiguration = RimoAxleConfiguration.frontFromSCE(Quantity.of(0.5, "SCE"));
    Tensor uvw = Tensors.fromString("{2[m*s^-1], 0.2[m*s^-1], -0.3[s^-1]}");
    axleConfiguration.wheel(0).adjoint(uvw);
    axleConfiguration.wheel(1).adjoint(uvw);
  }

  public void testFront() {
    AxleConfiguration axleConfiguration = RimoAxleConfiguration.frontFromSCE(Quantity.of(0.2, "SCE"));
    Chop._10.requireClose( //
        axleConfiguration.wheel(0).local(), //
        Tensors.fromString("{1.19[m], +0.48[m], 0.194912248}"));
    Chop._10.requireClose( //
        axleConfiguration.wheel(1).local(), //
        Tensors.fromString("{1.19[m], -0.48[m], 0.170696808}"));
  }

  public void testTwdOdometry() throws ClassNotFoundException, IOException {
    TwdOdometry twdOdometry = Serialization.copy(new TwdOdometry(RimoAxleConfiguration.rear()));
    Scalar tangentSpeed = twdOdometry.tangentSpeed(Tensors.fromString("{1[s^-1], 1[s^-1]}"));
    Chop._12.requireClose(tangentSpeed, Quantity.of(0.12, SI.VELOCITY));
    Scalar turningRate = twdOdometry.turningRate(Tensors.fromString("{1.2[s^-1], 3[s^-1]}"));
    Chop._12.requireClose(turningRate, Quantity.of(0.2, SI.PER_SECOND));
    Tensor velocity = twdOdometry.velocity(Tensors.fromString("{1.2[s^-1], 3[s^-1]}"));
    Chop._12.requireClose(velocity, Tensors.fromString("{0.252[m*s^-1], 0.0[m*s^-1], 0.2[s^-1]}"));
  }
}
