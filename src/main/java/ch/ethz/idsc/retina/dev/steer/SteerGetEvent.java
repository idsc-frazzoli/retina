// code by jph
package ch.ethz.idsc.retina.dev.steer;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.zhkart.DataEvent;

/** information received from micro-autobox about steering */
public class SteerGetEvent extends DataEvent {
  public static final int LENGTH = 44;
  // ---
  // TODO NRJ document variables
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

  @Override
  protected void insert(ByteBuffer byteBuffer) {
    byteBuffer.putFloat(motAsp_CANInput);
    byteBuffer.putFloat(motAsp_Qual);
    byteBuffer.putFloat(tsuTrq_CANInput);
    byteBuffer.putFloat(tsuTrq_Qual);
    byteBuffer.putFloat(refMotTrq_CANInput);
    byteBuffer.putFloat(estMotTrq_CANInput);
    byteBuffer.putFloat(estMotTrq_Qual);
    // ---
    byteBuffer.putFloat(gcpRelRckPos);
    byteBuffer.putFloat(gcpRelRckQual);
    byteBuffer.putFloat(gearRat);
    byteBuffer.putFloat(halfRckPos);
  }

  @Override
  protected int length() {
    return LENGTH;
  }

  public double getSteeringAngle() {
    // TODO NRJ not final formula for steering angle
    return gcpRelRckPos;
  }
}
