// code by edo
// code adapted by jph
package ch.ethz.idsc.owl.car.model;

import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;

public class CarState {
  public final Scalar Ux; // 1 long speed body frame [m/s]
  public final Scalar Uy; // 2 lateral speed body frame [m/s]
  public final Scalar r; // 3 yawing rate [rad/s]
  public final Scalar Ksi; // 4 heading of the car [rad]
  public final Scalar px; // 5 pos [m]
  public final Scalar py; // 6 pos [m]
  /** rate for each tire, unmodifiable */
  public final Tensor omega; // [rad/s]

  public CarState(Tensor x) {
    if (x.length() < 6 + 3) // assume that car has at least 3 tires
      throw TensorRuntimeException.of(x);
    // ---
    Ux = x.Get(0);
    Uy = x.Get(1);
    r = x.Get(2);
    Ksi = x.Get(3);
    px = x.Get(4);
    py = x.Get(5);
    omega = x.extract(6, x.length()).unmodifiable();
  }

  /** @return state encoded as vector for input to {@link StateSpaceModel} */
  public Tensor asVector() {
    return Join.of( //
        Tensors.of( //
            Ux, Uy, //
            r, Ksi, //
            px, py), //
        omega);
  }

  public Tensor u_2d() {
    return Tensors.of(Ux, Uy);
  }

  public Tensor u_3d() {
    return Tensors.of(Ux, Uy, RealScalar.ZERO);
  }

  public Tensor rate_3d() {
    return Tensors.of(RealScalar.ZERO, RealScalar.ZERO, r);
  }

  public Tensor se2() {
    return Tensors.of(px, py, Ksi);
  }
}
