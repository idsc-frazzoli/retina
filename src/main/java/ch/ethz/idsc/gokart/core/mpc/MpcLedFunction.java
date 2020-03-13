package ch.ethz.idsc.gokart.core.mpc;

import java.util.function.BiFunction;

import ch.ethz.idsc.gokart.dev.led.LEDStatus;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.led.LEDLcm;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Sign;

public enum MpcLedFunction implements BiFunction<Scalar, Scalar, LEDStatus> {
  DETAILED {
    public LEDStatus apply(Scalar referenceAngle, Scalar currAngle) {
      int refIdx = angleToIdx(referenceAngle);
      int valIdx = angleToIdx(currAngle);
      System.out.println("Steer msg: " + refIdx + ", Pwr Steer: " + valIdx);
      return new LEDStatus(refIdx, valIdx);
    }
  },
  SPARSE {
    public LEDStatus apply(Scalar referenceAngle, Scalar currAngle) {
      Scalar diff = referenceAngle.subtract(currAngle);
      Scalar absDiff = diff.abs();
      Boolean signDiff = Sign.isPositiveOrZero(diff);
      if (Sign.isPositiveOrZero(absDiff.subtract(MAX_DIFF))) {
        if (signDiff) {
          int refIdx = 0;// angleToIdx(referenceAngle);
          int valIdx = 9;// angleToIdx(currAngle);
          System.out.println("Steer msg: " + refIdx + ", Pwr Steer: " + valIdx);
          try {
            LEDLcm.publish(GokartLcmChannel.LED_STATUS, new LEDStatus(refIdx, valIdx));
          } catch (Exception e) {
            e.printStackTrace();
          }
        } else {
          int refIdx = 18;// angleToIdx(referenceAngle);
          int valIdx = 0;// angleToIdx(currAngle);
          System.out.println("Steer msg: " + refIdx + ", Pwr Steer: " + valIdx);
          try {
            LEDLcm.publish(GokartLcmChannel.LED_STATUS, new LEDStatus(refIdx, valIdx));
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      } else {
        int refIdx = 14;// angleToIdx(referenceAngle);
        int valIdx = 14;// angleToIdx(currAngle);
        System.out.println("Steer msg: " + refIdx + ", Pwr Steer: " + valIdx);
        try {
          LEDLcm.publish(GokartLcmChannel.LED_STATUS, new LEDStatus(refIdx, valIdx));
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  };

  private static final Clip ANGLE_RANGE = //
      Clips.interval(Quantity.of(-0.5, SteerPutEvent.UNIT_ENCODER), Quantity.of(0.5, SteerPutEvent.UNIT_ENCODER));

  private static int angleToIdx(Scalar angle) {
    double angleCorr = ANGLE_RANGE.apply(angle).number().doubleValue();
    return (int) Math.round((0.5 - angleCorr) * (LEDStatus.NUM_LEDS - 1));
  }
}
