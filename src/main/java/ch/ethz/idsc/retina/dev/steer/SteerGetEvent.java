// code by jph
package ch.ethz.idsc.retina.dev.steer;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.zhkart.DataEvent;

/** information received from micro-autobox about steering
 * 
 * the manufacturer of the steering column does <em>not</em>
 * share details about the exact meaning of the values sent by
 * the device, therefore our documentation also lacks clues. */
public class SteerGetEvent extends DataEvent {
  /* package */ static final int LENGTH = 44;
  // ---
  public final float motAsp_CANInput;
  public final float motAsp_Qual; // constant 2.0
  public final float tsuTrq_CANInput;
  public final float tsuTrq_Qual; // constant 2.0
  /** the difference between refMotTrq_CANInput and estMotTrq_CANInput
   * is typically small */
  public final float refMotTrq_CANInput;
  public final float estMotTrq_CANInput;
  public final float estMotTrq_Qual; // constant 2.0
  // ---
  /** angular position relative to fixed but initially unknown offset */
  private final float gcpRelRckPos;
  public final float gcpRelRckQual; // constant 2.0
  public final float gearRat; // constant 22.0
  public final float halfRckPos; // constant 72.0

  /** @param byteBuffer from which constructor reads 44 bytes */
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

  /** gcpRelRckPos == offset + factor * steering_angle
   * 
   * the offset has to be determined in a calibration procedure
   * 
   * @return relative angular position with respect to positive z-axis, i.e.
   * increasing values correspond to ccw rotation */
  public float getGcpRelRckPos() {
    return gcpRelRckPos;
  }
}
