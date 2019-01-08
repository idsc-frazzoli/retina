// code by edo
// code adapted by jph
package ch.ethz.idsc.owl.car.model;

import ch.ethz.idsc.owl.car.core.TrackInterface;
import ch.ethz.idsc.owl.car.core.VehicleModel;
import ch.ethz.idsc.owl.math.Deadzone;
import ch.ethz.idsc.owl.math.StateSpaceModel;
import ch.ethz.idsc.sophus.planar.Cross2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Chop;
import ch.ethz.idsc.tensor.sca.Round;

/** the matlab code applies a rate limiter to u
 * if this is beneficial for stability, the limiter should
 * be a layer outside of the state space model */
public class CarStateSpaceModel implements StateSpaceModel {
  private final VehicleModel vehicleModel;
  private final TrackInterface trackInterface;

  /** @param vehicleModel
   * @param mu friction coefficient of tire on road */
  public CarStateSpaceModel(VehicleModel vehicleModel, TrackInterface trackInterface) {
    this.vehicleModel = vehicleModel;
    this.trackInterface = trackInterface;
  }

  private long tic = 0;

  @Override
  public Tensor f(Tensor x, Tensor u) {
    // u may need to satisfy certain conditions with respect to previous u
    CarState carState = new CarState(x);
    CarControl carControl = vehicleModel.createControl(u);
    TireForces tire = new TireForces( //
        vehicleModel, carState, carControl, trackInterface.mu(carState.asVector()));
    BrakeTorques brakeTorques = new BrakeTorques(vehicleModel, carState, carControl, tire);
    // ---
    final Scalar gForce = vehicleModel.mass().multiply(PhysicalConstants.G_EARTH);
    // TODO friction from drag
    final Tensor total = Total.of(tire.Forces);
    {
      Scalar dF_z = total.Get(2).subtract(gForce);
      if (Scalars.nonZero((Scalar) dF_z.map(Chop.below(1e-5))))
        System.out.println("dF_z=" + dF_z);
    }
    // (1.1)
    // TODO at the moment muRoll == 0!
    final Scalar rollFric = gForce.multiply(vehicleModel.muRoll());
    Deadzone deadzone = Deadzone.of(rollFric.negate(), rollFric);
    Tensor dir = total.extract(0, 2).map(deadzone);
    // formula for dux, duy could be vectorized
    final Scalar dux = dir.Get(0).subtract(vehicleModel.coulombFriction(carState.Ux)).divide(vehicleModel.mass());
    final Scalar duy = dir.Get(1).subtract(RealScalar.ZERO.multiply(vehicleModel.coulombFriction(carState.Uy))).divide(vehicleModel.mass());
    // ---
    Scalar dr;
    {
      Tensor torque = tire.torque();
      if (!tire.isTorqueConsistent() || !tire.isFzConsistent()) {
        long toc = System.currentTimeMillis();
        if (tic + 987 <= toc) {
          tic = toc;
          System.out.println("---");
          System.out.println("Tq=" + torque.map(Round._2));
          Scalar f03 = tire.Forces.Get(0, 2).add(tire.Forces.Get(3, 2));
          Scalar f12 = tire.Forces.Get(1, 2).add(tire.Forces.Get(2, 2));
          System.out.println("Fz=" + Tensors.of(f03, f12).map(Round._2));
        }
      }
      dr = torque.Get(2).multiply(vehicleModel.Iz_invert());
    }
    Tensor dp = RotationMatrix.of(carState.Ksi).dot(carState.u_2d());
    // ---
    Tensor dw = Tensors.vector(index -> //
    carControl.throttleV.Get(index).add(brakeTorques.torque(index)) //
        .subtract(vehicleModel.wheel(index).radius().multiply(tire.fwheel.Get(index, 0))) //
        .multiply(vehicleModel.wheel(index).Iw_invert()), //
        vehicleModel.wheels());
    // ---
    // change of coordinates
    Tensor ucd = Cross2D.of(carState.u_2d()).multiply(carState.r); // [Ux Uy 0] x [0 0 r]
    Tensor fxu = Join.of( //
        Tensors.of(dux, duy).subtract(ucd), // F/m +
        Tensors.of(dr, carState.r, dp.Get(0), dp.Get(1)), dw);
    // the observation is that dwXY oscillate a lot!
    // this is consistent with the MATLAB code
    // if (Scalars.lessThan(RealScalar.of(1e4), Norm.Infinity.of(fxu)))
    // System.out.println(fxu);
    return fxu;
  }

  @Override
  public Scalar getLipschitz() {
    return RealScalar.ONE; // null
  }
}
