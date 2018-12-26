// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.util.data.OfflineVectorInterface;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.Unit;

/* package */ public class GokartControl implements MPCNativeInsertable, OfflineVectorInterface {
  private static final Unit SCE_PER_SECOND = SteerPutEvent.UNIT_ENCODER.add(SI.PER_SECOND);
  private final float uL;
  private final float uR;
  private final float udotS;
  private final float uB;
  private final float aB;
  private final boolean directMotorControl;

  public GokartControl(float uL, float uR, float udotS, float uB) {
    this.uL = uL;
    this.uR = uR;
    this.udotS = udotS;
    this.uB = uB;
    this.aB = 0;
    this.directMotorControl = true;
  }

  public GokartControl(float aB, float udotS) {
    this.uL = 0;
    this.uR = 0;
    this.udotS = udotS;
    this.uB = 0;
    this.aB = aB;
    this.directMotorControl = false;
  }

  public Scalar getuL() {
    return Quantity.of(uL, NonSI.ARMS);
  }

  public Scalar getuR() {
    return Quantity.of(uR, NonSI.ARMS);
  }

  public Scalar getudotS() {
    return Quantity.of(udotS, SCE_PER_SECOND);
  }

  public Scalar getuB() {
    return RealScalar.of(uB);
  }

  public Scalar getaB() {
    return Quantity.of(aB, SI.ACCELERATION);
  }

  public GokartControl(ByteBuffer byteBuffer) {
    uL = byteBuffer.getFloat();
    uR = byteBuffer.getFloat();
    udotS = byteBuffer.getFloat();
    uB = byteBuffer.getFloat();
    aB = byteBuffer.getFloat();
    directMotorControl = Math.signum(aB) == 0;
  }

  @Override
  public void insert(ByteBuffer byteBuffer) {
    byteBuffer.putFloat(uL);
    byteBuffer.putFloat(uR);
    byteBuffer.putFloat(udotS);
    byteBuffer.putFloat(uB);
    byteBuffer.putFloat(aB);
  }

  @Override
  public int length() {
    return 5 * 4;
  }

  @Override
  public Tensor asVector() {
    if (directMotorControl)
      return Tensors.of(//
          getuL(), //
          getuR(), //
          getudotS(), //
          getuB());
    return Tensors.of(//
        getudotS(), //
        getaB());
  }

  @Override
  public String toString() {
    return "Control:\n" + asVector().toString() + "\n";
  }
}
