// code by jph
package ch.ethz.idsc.owl.car.model;

import ch.ethz.idsc.owl.car.core.VehicleModel;
import ch.ethz.idsc.owl.car.math.AckermannSteering;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Join;
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
    // System.out.println("angle");
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
    AckermannSteering ackermannSteering = new AckermannSteering(p1L.Get(0), p1L.Get(1));
    return ackermannSteering.pair(delta).append(RealScalar.ZERO).append(RealScalar.ZERO);
  }

  private static Tensor _angles_frontParallel(Scalar delta) {
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
    AckermannSteering ackermannSteering = new AckermannSteering(p2L.Get(0), p2L.Get(1));
    return Join.of(Array.zeros(2), ackermannSteering.pair(delta));
  }

  private Tensor _angles_both(Scalar delta) {
    Tensor p1L = wheel(0).lever();
    Tensor p1R = wheel(1).lever();
    Tensor p2L = wheel(2).lever();
    Tensor p2R = wheel(3).lever();
    return Tensors.of( //
        new AckermannSteering(p1L.Get(0), p1L.Get(1)).angle(delta), //
        new AckermannSteering(p1R.Get(0), p1R.Get(1)).angle(delta), //
        new AckermannSteering(p2L.Get(0), p2L.Get(1)).angle(delta.negate()), //
        new AckermannSteering(p2R.Get(0), p2R.Get(1)).angle(delta.negate()) //
    );
  }
}
