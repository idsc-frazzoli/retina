// code by jph
package ch.ethz.idsc.gokart.dev.steer;

import ch.ethz.idsc.retina.util.math.PiecewiseLinearFunction;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;

/* package */ enum SteerGainsSchedule {
  INSTANCE;
  // ---
  private final ScalarTensorFunction scalarTensorFunction;

  private SteerGainsSchedule() {
    Tensor table = ResourceData.of("/dev/steer/gains.csv");
    scalarTensorFunction = PiecewiseLinearFunction.of( //
        table.get(Tensor.ALL, 0), //
        Tensor.of(table.stream().map(row -> row.extract(1, 4))));
  }

  /* package */ Tensor getTriple(Scalar scalar) {
    return scalarTensorFunction.apply(scalar);
  }

  SteerGains getGains(Scalar scalar) {
    return new SteerGains(getTriple(scalar));
  }
}
