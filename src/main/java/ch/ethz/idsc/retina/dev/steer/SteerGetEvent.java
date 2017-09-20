// code by jph
package ch.ethz.idsc.retina.dev.steer;

import java.io.Serializable;
import java.nio.ByteBuffer;

/** information received from micro-autobox about steering */
public class SteerGetEvent implements Serializable {
  public static final int LENGTH = 44;
  // ---
  public final float motAsp_CANInput;
  public final float motAsp_Qual;
  public final float tsuTrq_CANInput;
  public final float tsuTrq_Qual;
  public final float refMotTrq_CANInput;
  public final float estMotTrq_CANInput;
  public final float estMotTrq_Qual;
  // ---
  public final float gcpRelRckPos;
  public final float gcpRelRckQual;
  public final float gearRat;
  public final float halfRckPos;

  public SteerGetEvent(ByteBuffer byteBuffer) {
    motAsp_CANInput = byteBuffer.getFloat();
    motAsp_Qual = byteBuffer.getFloat();
    tsuTrq_CANInput = byteBuffer.getFloat();
    tsuTrq_Qual = byteBuffer.getFloat();
    refMotTrq_CANInput = byteBuffer.getFloat();
    estMotTrq_CANInput = byteBuffer.getFloat();
    estMotTrq_Qual = byteBuffer.getFloat();
    // ---
    gcpRelRckPos = byteBuffer.getFloat();
    gcpRelRckQual = byteBuffer.getFloat();
    gearRat = byteBuffer.getFloat();
    halfRckPos = byteBuffer.getFloat();
  }

  public void encode(ByteBuffer byteBuffer) {
  }

  public double getSteeringAngle() {
    return gcpRelRckPos;
    // TODO NRJ Not final formula for steering angle
  }
}
