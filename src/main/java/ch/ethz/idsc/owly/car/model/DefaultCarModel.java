// code by jph
package ch.ethz.idsc.owly.car.model;

import ch.ethz.idsc.owly.car.core.VehicleModel;
import ch.ethz.idsc.owly.car.math.SteeringWheelAngle;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.ConvexHull;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.sca.Sign;

public abstract class DefaultCarModel implements VehicleModel {
  @Override
  public final int wheels() {
    return 4;
  }

  @Override
  public final Scalar coulombFriction(Scalar speed) {
    return Sign.of(speed).multiply(b().multiply(speed.abs()).add(fric()));
  }

  @Override
  public final Tensor angles(Scalar delta) {
    switch (steering()) {
    case FRONT:
      return _angles_front(delta);
    case FRONT_PARALLEL:
      return _angles_frontParallel(delta);
    case REAR:
      return _angles_rear(delta);
    case BOTH:
      return _angles_both(delta);
    default:
      break;
    }
    return null;
  }

  @Override
  public Tensor footprint() {
    Tensor hull = Tensors.empty();
    for (int index = 0; index < wheels(); ++index)
      hull.append(wheel(index).lever().extract(0, 2));
    return ConvexHull.of(hull);
  }

  /***************************************************/
  /** @return dynamic friction coefficient N/(m/s) */
  public abstract Scalar b();

  /** @return coulomb friction */
  public abstract Scalar fric();

  /** @return */
  public abstract CarSteering steering();

  /***************************************************/
  // helper functions
  private Tensor _angles_front(Scalar delta) {
    Tensor rear_center = Mean.of(Tensors.vector(i -> wheel(2 + i).lever(), 2));
    Tensor p1L = wheel(0).lever().subtract(rear_center);
    Tensor p1R = wheel(1).lever().subtract(rear_center);
    // TODO replace by ackermann steering
    return Tensors.of( //
        SteeringWheelAngle.of(p1L.Get(1).divide(p1L.Get(0)), delta), // 1L
        SteeringWheelAngle.of(p1R.Get(1).divide(p1R.Get(0)), delta), // 1R
        RealScalar.ZERO, // 2L
        RealScalar.ZERO // 2R
    );
  }

  private Tensor _angles_frontParallel(Scalar delta) {
    return Tensors.of( //
        delta, // 1L
        delta, // 1R
        RealScalar.ZERO, // 2L
        RealScalar.ZERO // 2R
    );
  }

  private Tensor _angles_rear(Scalar delta) {
    Tensor front_center = Mean.of(Tensors.vector(i -> wheel(0 + i).lever(), 2));
    Tensor p2L = wheel(2).lever().subtract(front_center);
    Tensor p2R = wheel(3).lever().subtract(front_center);
    return Tensors.of( //
        RealScalar.ZERO, // 1L
        RealScalar.ZERO, // 1R
        SteeringWheelAngle.of(p2L.Get(1).divide(p2L.Get(0)), delta.negate()), // 2L
        SteeringWheelAngle.of(p2R.Get(1).divide(p2R.Get(0)), delta.negate()) // 2R
    );
  }

  private Tensor _angles_both(Scalar delta) {
    Tensor p1L = wheel(0).lever();
    Tensor p1R = wheel(1).lever();
    Tensor p2L = wheel(2).lever();
    Tensor p2R = wheel(3).lever();
    return Tensors.of( //
        SteeringWheelAngle.of(p1L.Get(1).divide(p1L.Get(0)), delta), //
        SteeringWheelAngle.of(p1R.Get(1).divide(p1R.Get(0)), delta), //
        SteeringWheelAngle.of(p2L.Get(1).divide(p2L.Get(0)), delta.negate()), //
        SteeringWheelAngle.of(p2R.Get(1).divide(p2R.Get(0)), delta.negate()) //
    );
  }
}
