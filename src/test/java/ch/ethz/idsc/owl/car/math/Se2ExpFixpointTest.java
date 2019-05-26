// code by jph
package ch.ethz.idsc.owl.car.math;

import java.util.Optional;

import ch.ethz.idsc.owl.math.map.Se2Bijection;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.sophus.group.Se2CoveringExponential;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2ExpFixpointTest extends TestCase {
  public void testSimple() {
    Tensor velocity = Tensors.fromString("{3[m*s^-1], .2[m*s^-1], 0.3[s^-1]}");
    Optional<Tensor> optional = Se2ExpFixpoint.of(velocity);
    for (Tensor _t : Subdivide.of(Quantity.of(-2.1, SI.SECOND), Quantity.of(10, SI.SECOND), 13)) {
      Se2Bijection se2Bijection = new Se2Bijection(Se2CoveringExponential.INSTANCE.exp(velocity.multiply(_t.Get())));
      Chop._10.requireClose(se2Bijection.forward().apply(optional.get()), optional.get());
    }
  }

  public void testSimple2() {
    Tensor velocity = Tensors.fromString("{-3[m*s^-1], 1.2[m*s^-1], -0.3[s^-1]}");
    Optional<Tensor> optional = Se2ExpFixpoint.of(velocity);
    for (Tensor _t : Subdivide.of(Quantity.of(-5.1, SI.SECOND), Quantity.of(10, SI.SECOND), 17)) {
      Se2Bijection se2Bijection = new Se2Bijection(Se2CoveringExponential.INSTANCE.exp(velocity.multiply(_t.Get())));
      Chop._10.requireClose(se2Bijection.forward().apply(optional.get()), optional.get());
    }
  }
}
