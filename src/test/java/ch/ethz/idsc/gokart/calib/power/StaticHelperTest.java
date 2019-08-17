// code by jph
package ch.ethz.idsc.gokart.calib.power;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.VectorQ;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clips;
import junit.framework.TestCase;

public class StaticHelperTest extends TestCase {
  public void testSmallL() {
    Tensor currents = StaticHelper.getMotorCurrents(Quantity.of(0.3, SI.ACCELERATION), Quantity.of(+0.1, SI.ONE), Quantity.of(3.3, SI.VELOCITY));
    VectorQ.requireLength(currents, 2);
    Clips.interval(230, 270).requireInside(Magnitude.ARMS.apply(currents.Get(0)));
    Clips.interval(440, 470).requireInside(Magnitude.ARMS.apply(currents.Get(1)));
  }

  public void testSmallR() {
    Tensor currents = StaticHelper.getMotorCurrents(Quantity.of(0.3, SI.ACCELERATION), Quantity.of(-0.1, SI.ONE), Quantity.of(3.3, SI.VELOCITY));
    VectorQ.requireLength(currents, 2);
    Clips.interval(230, 270).requireInside(Magnitude.ARMS.apply(currents.Get(1)));
    Clips.interval(440, 470).requireInside(Magnitude.ARMS.apply(currents.Get(0)));
  }

  public void testMid() {
    Tensor currents = StaticHelper.getMotorCurrents(Quantity.of(1.3, SI.ACCELERATION), Quantity.of(0.1, SI.ONE), Quantity.of(3.3, SI.VELOCITY));
    VectorQ.requireLength(currents, 2);
    Clips.interval(770, 810).requireInside(Magnitude.ARMS.apply(currents.Get(0)));
    Clips.interval(970, 1020).requireInside(Magnitude.ARMS.apply(currents.Get(1)));
  }

  public void testExtreme() {
    Tensor currents = StaticHelper.getMotorCurrents(Quantity.of(10.3, SI.ACCELERATION), Quantity.of(0.8, SI.ONE), Quantity.of(0.0, SI.VELOCITY));
    VectorQ.requireLength(currents, 2);
    Clips.interval(2200, 2400).requireInside(Magnitude.ARMS.apply(currents.Get(0)));
    Clips.interval(2200, 2400).requireInside(Magnitude.ARMS.apply(currents.Get(1)));
  }
}
