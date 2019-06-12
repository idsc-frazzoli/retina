// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import ch.ethz.idsc.sophus.filter.CenterFilter;
import ch.ethz.idsc.sophus.filter.ga.GeodesicCenter;
import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.sophus.math.win.WindowCenterSampler;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;
import ch.ethz.idsc.tensor.sca.win.GaussianWindow;

public enum GokartPoseSmoothing implements TensorUnaryOperator {
  INSTANCE;
  // ---
  private static final int HALF_WIDTH = 6;
  private static final TensorUnaryOperator TENSOR_UNARY_OPERATOR = //
      GeodesicCenter.of(Se2Geodesic.INSTANCE, WindowCenterSampler.of(GaussianWindow.FUNCTION));

  @Override // from TensorUnaryOperator
  public Tensor apply(Tensor tensor) {
    return CenterFilter.of(TENSOR_UNARY_OPERATOR, HALF_WIDTH).apply(tensor);
  }
}
