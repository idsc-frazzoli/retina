// code by edo
// code adapted by jph
package ch.ethz.idsc.owl.car.model;

import ch.ethz.idsc.owl.car.core.VehicleModel;
import ch.ethz.idsc.owl.car.math.RobustSlip;
import ch.ethz.idsc.sophus.planar.Cross2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.alg.Join;
import ch.ethz.idsc.tensor.lie.Cross;
import ch.ethz.idsc.tensor.lie.Rodrigues;
import ch.ethz.idsc.tensor.lie.RotationMatrix;
import ch.ethz.idsc.tensor.mat.LinearSolve;
import ch.ethz.idsc.tensor.red.Total;
import ch.ethz.idsc.tensor.sca.Chop;

/** implementation has been verified through several tests */
public class TireForces {
  private static final Tensor AFFINE_ONE = Tensors.vector(1);
  private static final Tensor SUM_ALL = Tensors.vector(1, 1, 1, 1).unmodifiable();
  private static final Tensor WEIGHT_STD = Tensors.vector(+1, -1, -1, +1).unmodifiable();
  // ---
  public final VehicleModel vehicleModel;
  public final CarState carState;
  public final Tensor Forces; // forces in car/body frame, matrix 4 x 3
  public final Tensor fwheel; // forces in wheel frame, matrix 4 x 3

  /** @param vehicleModel
   * @param carState
   * @param carControl
   * @param mu friction coefficient of tire on road/ground, see FrictionCoefficients */
  public TireForces(VehicleModel vehicleModel, CarState carState, CarControl carControl, Scalar mu) {
    this.vehicleModel = vehicleModel;
    this.carState = carState;
    final Tensor angles = vehicleModel.angles(carControl.delta).unmodifiable();
    // ---
    Tensor mus = Tensors.vector(index -> //
    new RobustSlip(vehicleModel.wheel(index).pacejka(), get_ui_2d(angles.Get(index), index), //
        vehicleModel.wheel(index).radius().multiply(carState.omega.Get(index))).slip(), 4).multiply(mu);
    final Tensor dir = Tensors.vector(index -> //
    Join.of(RotationMatrix.of(angles.Get(index)).dot(mus.get(index)), AFFINE_ONE), 4);
    // ---
    final Tensor fbodyZ;
    {
      Tensor levers = Tensors.vector(i -> vehicleModel.wheel(i).lever(), 4);
      Tensor rotX_z = levers.get(Tensor.ALL, 1);
      Tensor rotX_y = levers.get(Tensor.ALL, 2).pmul(dir.get(Tensor.ALL, 1)); // z coordinate of tire contact * dir_y
      Tensor rotY_z = levers.get(Tensor.ALL, 0);
      Tensor rotY_x = levers.get(Tensor.ALL, 2).pmul(dir.get(Tensor.ALL, 0)); // z coordinate of tire contact * dir_x
      Tensor Lhs = Tensors.of( //
          rotX_z.subtract(rotX_y), // no rotation around X
          rotY_z.subtract(rotY_x), // no rotation around Y
          SUM_ALL, // compensate g-force
          WEIGHT_STD // weight transfer LONGTERM geometry of COG?
      );
      // System.out.println("det=" + Det.of(Lhs));
      Tensor rhs = Array.zeros(4);
      Scalar gForce = vehicleModel.mass().multiply(PhysicalConstants.G_EARTH);
      rhs.set(gForce, 2);
      fbodyZ = LinearSolve.of(Lhs, rhs);
    }
    // ---
    // if (false) {
    // Scalar lF = params.levers().Get(0, 0);
    // Scalar lR = params.levers().Get(2, 0).negate();
    // Scalar lR_lF = lF.add(lR).multiply(RealScalar.of(2));
    // Fz1L = lR.divide(lR_lF).multiply(params.gForce());
    // Fz1R = lR.divide(lR_lF).multiply(params.gForce());
    // Fz2L = lF.divide(lR_lF).multiply(params.gForce());
    // Fz2R = lF.divide(lR_lF).multiply(params.gForce());
    // }
    fwheel = fbodyZ.pmul(mus).unmodifiable();
    Forces = fbodyZ.pmul(dir).unmodifiable();
  }

  /** @return torque on vehicle at center of mass */
  public Tensor torque() {
    Tensor tensor = Array.zeros(3);
    for (int index = 0; index < vehicleModel.wheels(); ++index)
      tensor = tensor.add(Cross.of(vehicleModel.wheel(index).lever(), Forces.get(index)));
    return tensor;
  }

  public boolean isTorqueConsistent() {
    return Chop._07.allZero(torque().extract(0, 2));
  }

  public boolean isFzConsistent() {
    Scalar f03 = Forces.Get(0, 2).add(Forces.Get(3, 2));
    Scalar f12 = Forces.Get(1, 2).add(Forces.Get(2, 2));
    return Chop._07.close(f03, f12);
  }

  public boolean isGForceConsistent() {
    Scalar gForce = vehicleModel.mass().multiply(PhysicalConstants.G_EARTH);
    return Chop._07.close(Total.of(Forces).Get(2), gForce);
  }

  /** @param delta angle of wheel
   * @param index of wheel
   * @return */
  private Tensor get_ui_2d(Scalar delta, int index) { // as in doc
    Tensor tangent_2 = carState.u_2d().add(Cross2D.of(vehicleModel.wheel(index).lever().extract(0, 2).multiply(carState.r)));
    return RotationMatrix.of(delta.negate()).dot(tangent_2);
  }

  /** implementation below is for full 3d rotations, but not used since
   * at the moment our car rotates in plane (only around z-axis)
   * 
   * @param delta
   * @param index
   * @return */
  /* package */ Tensor get_ui_3(Scalar delta, int index) { // as in doc
    Tensor rotation_3 = Rodrigues.exp(Tensors.of(RealScalar.ZERO, RealScalar.ZERO, delta.negate()));
    Tensor tangent_3 = carState.u_3d().add(Cross.of(carState.rate_3d(), vehicleModel.wheel(index).lever()));
    return rotation_3.dot(tangent_3).extract(0, 2);
  }
}
