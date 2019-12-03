// code by am, jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Series;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.ScalarUnaryOperator;

/** constants in this class were determined in experimentation */
/* package */ class SteerFeedForwardConfig {
  public static final SteerFeedForwardConfig GLOBAL = AppResources.load(new SteerFeedForwardConfig());
  /***************************************************/
  /** https://github.com/idsc-frazzoli/retina/files/3265874/20190521_restoring_force_of_steering.pdf */
  public final Scalar linear = Quantity.of(+0.9581478188758055, "SCT*SCE^-1");
  public final Scalar cubic = Quantity.of(-0.9281077083540995, "SCT*SCE^-3");

  /***************************************************/
  /** function returns needed torque to compensate restoring force of steering.
   * 
   * In the power steering module, the return value contributes (by addition) to
   * the total torque.
   * 
   * @return function that maps quantities with unit "SCE" to quantities with unit "SCT" */
  public ScalarUnaryOperator series() {
    return Series.of(Tensors.of( //
        Quantity.of(0.0, "SCT"), //
        linear, //
        Quantity.of(0.0, "SCT*SCE^-2"), //
        cubic));
  }
}
