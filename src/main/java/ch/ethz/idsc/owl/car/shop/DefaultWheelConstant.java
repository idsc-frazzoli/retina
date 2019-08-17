// code by jph
package ch.ethz.idsc.owl.car.shop;

import ch.ethz.idsc.owl.car.core.WheelInterface;
import ch.ethz.idsc.owl.car.slip.Pacejka3;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** class holds invariant parameters of wheel */
/* package */ class DefaultWheelConstant implements WheelInterface {
  private final Scalar radius;
  private final Scalar width;
  private final Scalar iw_invert;
  private final Pacejka3 pacejka3;
  private final Tensor lever;

  public DefaultWheelConstant(Scalar radius, Scalar width, Scalar iw, Pacejka3 pacejka3, Tensor lever) {
    this.radius = radius;
    this.width = width;
    this.iw_invert = iw.reciprocal();
    this.pacejka3 = pacejka3;
    this.lever = lever.unmodifiable();
  }

  @Override
  public Tensor lever() {
    return lever;
  }

  @Override
  public Scalar radius() {
    return radius;
  }

  @Override
  public Scalar width() {
    return width;
  }

  @Override
  public Scalar Iw_invert() {
    return iw_invert;
  }

  @Override
  public Pacejka3 pacejka() {
    return pacejka3;
  }
}
