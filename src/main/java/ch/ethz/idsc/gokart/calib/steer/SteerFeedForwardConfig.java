// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Series;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

public class SteerFeedForwardConfig {
  public static final SteerFeedForwardConfig GLOBAL = AppResources.load(new SteerFeedForwardConfig());
  /***************************************************/
  public Scalar linear = Quantity.of(+0.9581478188758055, "SCT*SCE^-1");
  public Scalar cubic = Quantity.of(-0.9281077083540995, "SCT*SCE^-3");

  /***************************************************/
  public ScalarUnaryOperator series() {
    return Series.of(Tensors.of( //
        RealScalar.ZERO, //
        linear, //
        RealScalar.ZERO, //
        cubic));
  }
}
