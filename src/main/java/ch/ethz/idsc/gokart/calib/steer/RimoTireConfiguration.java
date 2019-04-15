// code by jph
package ch.ethz.idsc.gokart.calib.steer;

import ch.ethz.idsc.gokart.offline.video.TireConfiguration;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

public enum RimoTireConfiguration implements TireConfiguration {
  FRONT(Quantity.of(0.230 * 0.5, SI.METER), Quantity.of(0.0650, SI.METER)), //
  _REAR(Quantity.of(0.240 * 0.5, SI.METER), Quantity.of(0.0975, SI.METER)), //
  ;
  // ---
  private final Scalar radius;
  private final Scalar halfWidth;

  private RimoTireConfiguration(Scalar radius, Scalar halfWidth) {
    this.radius = radius;
    this.halfWidth = halfWidth;
  }

  @Override // from TireConfiguration
  public Scalar radius() {
    return radius;
  }

  @Override // from TireConfiguration
  public Scalar halfWidth() {
    return halfWidth;
  }

  @Override // from TireConfiguration
  public Tensor footprint() {
    double TR = radius().number().doubleValue();
    double TW = halfWidth().number().doubleValue();
    return Tensors.matrixDouble( //
        new double[][] { { TR, TW }, { -TR, TW }, { -TR, -TW }, { TR, -TW } });
  }
}
