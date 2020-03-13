// code by ta, gjoel
package ch.ethz.idsc.gokart.core.mpc;

import java.util.function.BiFunction;

import ch.ethz.idsc.gokart.dev.led.LEDIndexHelper;
import ch.ethz.idsc.gokart.dev.led.LEDStatus;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

public enum MpcLedFunction implements BiFunction<Scalar, Scalar, LEDStatus> {
  DETAILED {
    public LEDStatus apply(Scalar referenceAngle, Scalar currAngle) {
      int refIdx = LEDIndexHelper.getIn(referenceAngle, ANGLE_RANGE);
      int valIdx = LEDIndexHelper.getIn(currAngle, ANGLE_RANGE);
      System.out.println("Steer msg: " + refIdx + ", Pwr Steer: " + valIdx);
      return new LEDStatus(refIdx, valIdx);
    }
  },
  SPARSE {
    public LEDStatus apply(Scalar referenceAngle, Scalar currAngle) {
      // TODO remove hard-coded indices
      // Scalar diff = referenceAngle.subtract(currAngle);
      // if (Scalars.lessEquals(Quantity.of(0.5, SteerPutEvent.UNIT_ENCODER), diff.abs())) {
      //   if (Sign.isPositiveOrZero(diff)) {
      //     int refIdx = 0; // LEDIndexHelper.getIn(referenceAngle, ANGLE_RANGE);
      //     int valIdx = 9; // LEDIndexHelper.getIn(currAngle, ANGLE_RANGE);
      //     System.out.println("Steer msg: " + refIdx + ", Pwr Steer: " + valIdx);
      //     return new LEDStatus(refIdx, valIdx);
      //   } else {
      //     int refIdx = 18; // LEDIndexHelper.getIn(referenceAngle, ANGLE_RANGE);
      //     int valIdx = 0; // LEDIndexHelper.getIn(currAngle, ANGLE_RANGE);
      //     System.out.println("Steer msg: " + refIdx + ", Pwr Steer: " + valIdx);
      //     return new LEDStatus(refIdx, valIdx);
      //   }
      // } else {
      //   int refIdx = 14; // LEDIndexHelper.getIn(referenceAngle, ANGLE_RANGE);
      //   int valIdx = 14; // LEDIndexHelper.getIn(currAngle, ANGLE_RANGE);
      //   System.out.println("Steer msg: " + refIdx + ", Pwr Steer: " + valIdx);
      //   return new LEDStatus(refIdx, valIdx);
      // }
      switch (Integer.signum(Scalars.compare(referenceAngle, currAngle))) {
      case -1:
        return new LEDStatus(18, 0);
      case 0:
        return new LEDStatus(14);
      case 1:
        return new LEDStatus(0, 9);
      default:
        return null;
      }
    }
  };

  // 0.5[SCE] could maybe be replaced by half of SteerColumnTracker::getIntervalWidth
  private static final Clip ANGLE_RANGE = Clips.absolute(Quantity.of(0.5, SteerPutEvent.UNIT_ENCODER));
}
