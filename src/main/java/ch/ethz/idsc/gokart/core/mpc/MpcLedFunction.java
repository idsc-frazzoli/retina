package ch.ethz.idsc.gokart.core.mpc;

import java.util.function.BiFunction;

import ch.ethz.idsc.gokart.dev.led.LEDStatus;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

public enum MpcLedFunction implements BiFunction<Scalar, Scalar,LEDStatus >{
  DETAILED{
    public LEDStatus apply(Scalar referenceAngle, Scalar currAngle) {
      int refIdx = angleToIdx(referenceAngle);
      int valIdx = angleToIdx(currAngle);
      System.out.println("Steer msg: " + refIdx + ", Pwr Steer: " + valIdx);
      return new LEDStatus(refIdx, valIdx);
    }
  };
  private static final Clip ANGLE_RANGE = //
  Clips.interval(Quantity.of(-0.5, SteerPutEvent.UNIT_ENCODER), Quantity.of(0.5, SteerPutEvent.UNIT_ENCODER));
  
  private static int angleToIdx(Scalar angle) {
    double angleCorr = ANGLE_RANGE.apply(angle).number().doubleValue();
    return (int) Math.round((0.5 - angleCorr) * (LEDStatus.NUM_LEDS - 1));
  }
  
}
