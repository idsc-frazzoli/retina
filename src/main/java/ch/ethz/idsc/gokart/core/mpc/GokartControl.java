// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.util.data.BufferInsertable;
import ch.ethz.idsc.retina.util.data.OfflineVectorInterface;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.Unit;

// TODO MH document all getter functions!
/* package */ class GokartControl implements BufferInsertable, OfflineVectorInterface {
  static final int LENGTH = 20;
  private static final Unit SCE_PER_SECOND = SteerPutEvent.UNIT_ENCODER.add(SI.PER_SECOND);
  // ---
  private final float uL;
  private final float uR;
  private final float udotS;
  private final float uB;
  private final float aB;

  /** ONLY FOR TESTING
   * 
   * @param uL
   * @param uR
   * @param udotS
   * @param uB */
  GokartControl(float uL, float uR, float udotS, float uB) {
    this.uL = uL;
    this.uR = uR;
    this.udotS = udotS;
    this.uB = uB;
    this.aB = 0;
  }

  /** ONLY FOR TESTING
   * 
   * @param aB
   * @param udotS */
  GokartControl(float aB, float udotS) {
    this.uL = 0;
    this.uR = 0;
    this.udotS = udotS;
    this.uB = 0;
    this.aB = aB;
  }

  public Scalar getuL() {
    return Quantity.of(uL, SI.ACCELERATION);
  }

  public Scalar getuR() {
    return Quantity.of(uR, SI.ACCELERATION);
  }

  public Scalar getudotS() {
    return Quantity.of(udotS, SCE_PER_SECOND);
  }

  public Scalar getuB() {
    return RealScalar.of(uB);
  }

  /** @return quantity with unit "m*s^-2" */
  public Scalar getaB() {
    return Quantity.of(aB, SI.ACCELERATION);
  }

  public GokartControl(ByteBuffer byteBuffer) {
    uL = byteBuffer.getFloat();
    uR = byteBuffer.getFloat();
    udotS = byteBuffer.getFloat();
    uB = byteBuffer.getFloat();
    aB = byteBuffer.getFloat();
  }

  @Override // from BufferInsertable
  public void insert(ByteBuffer byteBuffer) {
    byteBuffer.putFloat(uL);
    byteBuffer.putFloat(uR);
    byteBuffer.putFloat(udotS);
    byteBuffer.putFloat(uB);
    byteBuffer.putFloat(aB);
  }

  @Override // from BufferInsertable
  public int length() {
    return LENGTH;
  }

  @Override
  public Tensor asVector() {
    return Tensors.vectorFloat( //
        uL, //
        uR, //
        udotS, //
        uB, //
        aB);
  }

  @Override
  public String toString() {
    return "Control:\n" + asVector().toString() + "\n";
  }
}
