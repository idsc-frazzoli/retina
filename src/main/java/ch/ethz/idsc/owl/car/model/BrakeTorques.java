// code by edo
// code adapted by jph
package ch.ethz.idsc.owl.car.model;

import ch.ethz.idsc.owl.car.core.VehicleModel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Sign;

public class BrakeTorques {
  private final Tensor tbreak;

  public BrakeTorques( //
      VehicleModel vehicleModel, CarState carState, CarControl carControl, TireForces tireForces) {
    Scalar _Tb1L = RealScalar.ZERO;
    Scalar _Tb1R = RealScalar.ZERO;
    Scalar _Tb2L = RealScalar.ZERO;
    Scalar _Tb2R = RealScalar.ZERO;
    // ---
    Scalar masterPress = carControl.brake;
    final Scalar pressF = masterPress;
    final Scalar pressR;
    // ---
    if (Scalars.lessEquals(masterPress, RealScalar.of(1.5))) {
      pressR = masterPress;
    } else {
      // TODO magic constants
      pressR = RealScalar.of(0.3).multiply(masterPress).add(RealScalar.of(1.05));
    }
    // ---
    if (Sign.isPositive(masterPress)) {
      if (Scalars.nonZero(carState.omega.Get(0))) {
        _Tb1L = pressF.multiply(vehicleModel.press2torF()).multiply(Sign.of(carState.omega.Get(0))).negate();
      } else {
        _Tb1L = tireForces.fwheel.Get(0, 0).multiply(vehicleModel.wheel(0).radius());
      }
      //
      if (Scalars.nonZero(carState.omega.Get(1))) {
        _Tb1R = pressF.multiply(vehicleModel.press2torF()).multiply(Sign.of(carState.omega.Get(1))).negate();
      } else {
        _Tb1R = tireForces.fwheel.Get(1, 0).multiply(vehicleModel.wheel(1).radius());
      }
      //
      if (Scalars.nonZero(carState.omega.Get(2))) {
        _Tb2L = pressR.multiply(vehicleModel.press2torR()).multiply(Sign.of(carState.omega.Get(2))).negate();
      } else {
        _Tb2L = tireForces.fwheel.Get(2, 0).multiply(vehicleModel.wheel(2).radius());
      }
      //
      if (Scalars.nonZero(carState.omega.Get(3))) {
        _Tb2R = pressR.multiply(vehicleModel.press2torR()).multiply(Sign.of(carState.omega.Get(3))).negate();
      } else {
        _Tb2R = tireForces.fwheel.Get(3, 0).multiply(vehicleModel.wheel(3).radius());
      }
    }
    // ---
    if (Sign.isPositive(carControl.handbrake)) {
      if (Scalars.nonZero(carState.omega.Get(2))) {
        _Tb2L = _Tb2L.subtract(carControl.handbrake.multiply(Sign.of(carState.omega.Get(2))));
      } else {
        _Tb2L = _Tb2L.subtract(tireForces.fwheel.Get(2, 0).multiply(vehicleModel.wheel(2).radius()));
      }
      //
      if (Scalars.nonZero(carState.omega.Get(3))) {
        _Tb2R = _Tb2R.subtract(carControl.handbrake.multiply(Sign.of(carState.omega.Get(3))));
      } else {
        _Tb2R = _Tb2R.subtract(tireForces.fwheel.Get(3, 0).multiply(vehicleModel.wheel(3).radius()));
      }
    }
    tbreak = Tensors.of(_Tb1L, _Tb1R, _Tb2L, _Tb2R).unmodifiable();
  }

  public Scalar torque(int index) {
    return tbreak.Get(index);
  }
}
