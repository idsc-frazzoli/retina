// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.util.data.OfflineVectorInterface;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.Unit;

/* package */ class GokartControl implements MPCNativeInsertable, OfflineVectorInterface {
  private static final Unit SCE_PER_SECOND = SteerPutEvent.UNIT_ENCODER.add(SI.PER_SECOND);
  private final float uL;
  private final float uR;
  private final float udotS;
  private final float uB;

  public GokartControl(float uL, float uR, float udotS, float uB) {
    this.uL = uL;
    this.uR = uR;
    this.udotS = udotS;
    this.uB = uB;
  }

  public Scalar getuL() {
    return Quantity.of(uL, NonSI.ARMS);
  }

  public Scalar getuR() {
    return Quantity.of(uR, NonSI.ARMS);
  }

  public Scalar getudotS() {
    return Quantity.of(uR, SCE_PER_SECOND);
  }

  public Scalar getuB() {
    return Quantity.of(uB, SI.ONE);
  }

  public GokartControl(ByteBuffer byteBuffer) {
    uL = byteBuffer.getFloat();
    uR = byteBuffer.getFloat();
    udotS = byteBuffer.getFloat();
    uB = byteBuffer.getFloat();
  }

  @Override
  public void insert(ByteBuffer byteBuffer) {
    byteBuffer.putFloat(uL);
    byteBuffer.putFloat(uR);
    byteBuffer.putFloat(udotS);
    byteBuffer.putFloat(uB);
  }

  @Override
  public int length() {
    return 4 * 4;
  }

  @Override
  public Tensor asVector() {
    return Tensors.of(//
        getuL(), //
        getuR(), //
        getudotS(), //
        getuB());
  }

  public String toString() {
    return "Control:\n" + asVector().toString() + "\n";
  }
}
