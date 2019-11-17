// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import java.io.IOException;

import ch.ethz.idsc.owl.car.core.WheelConfiguration;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.io.Serialization;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class TricycleFrontAxleConfigurationTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    TricycleFrontAxleConfiguration tricycleFrontAxleConfiguration = //
        Serialization.copy(new TricycleFrontAxleConfiguration(Quantity.of(0.3, "SCE")));
    WheelConfiguration wheelConfiguration = tricycleFrontAxleConfiguration.wheel(0);
    Tensor tensor = wheelConfiguration.adjoint(Tensors.fromString("{2[m*s^-1], 3[m*s^-1], 1[s^-1]}"));
    VectorQ.requireLength(tensor, 3);
  }

  public void testStraight() {
    TricycleFrontAxleConfiguration tricycleFrontAxleConfiguration = //
        new TricycleFrontAxleConfiguration(Quantity.of(0, "SCE"));
    WheelConfiguration wheelConfiguration = tricycleFrontAxleConfiguration.wheel(0);
    Tensor vel = Tensors.fromString("{2[m*s^-1], 3[m*s^-1], 0[s^-1]}");
    Chop._12.requireClose(wheelConfiguration.adjoint(vel), vel);
  }
}
