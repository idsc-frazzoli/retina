// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Objects;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public class MPCSimpleBraking extends MPCBraking {
  MPCStateEstimationProvider mpcStateProvider;
  int inext = 0;

  @Override
  public Scalar getBraking(Scalar time) {
    ControlAndPredictionStep cnsStep = getStep(time);
    if (Objects.isNull(cnsStep))
      return Quantity.of(0, SI.ONE);
    return getStep(time).control.getuB();
  }

  @Override
  public void setStateProvider(MPCStateEstimationProvider mpcstateProvider) {
    this.mpcStateProvider = mpcstateProvider;
  }
}
