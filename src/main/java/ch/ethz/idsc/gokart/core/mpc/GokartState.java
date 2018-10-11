// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.util.data.OfflineVectorInterface;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ class GokartState implements OfflineVectorInterface, MPCNativeInsertable {
  /** forward velocity in gokart frame with unit m*s^1 */
  public final Scalar Ux;
  /** sidewards velocity in gokart frame with unit m*s^1 */
  public final Scalar Uy;
  /** angular velocity with unit rad*s^-1 */
  public final Scalar dotPsi;
  /** global position in X direction with unit m */
  public final Scalar X;
  /** global position in Y direction with unit m */
  public final Scalar Y;
  /** orientation with unit rad */
  public final Scalar Psi;
  /** left rear wheel speed */
  public final Scalar w2L;
  /** right rear wheel speed */
  public final Scalar w2R;

  public GokartState(//
      Scalar Ux, //
      Scalar Uy, //
      Scalar dotPsi, //
      Scalar X, //
      Scalar Y, //
      Scalar Psi, //
      Scalar w2L, //
      Scalar w2R) {
    this.Ux = Ux;
    this.Uy = Uy;
    this.dotPsi = dotPsi;
    this.X = X;
    this.Y = Y;
    this.Psi = Psi;
    this.w2L = w2L;
    this.w2R = w2R;
    checkUnits();
  }

  public GokartState(Tensor GokartStateTensor) {
    Ux = GokartStateTensor.Get(0);
    Uy = GokartStateTensor.Get(1);
    dotPsi = GokartStateTensor.Get(2);
    X = GokartStateTensor.Get(3);
    Y = GokartStateTensor.Get(4);
    Psi = GokartStateTensor.Get(5);
    w2L = GokartStateTensor.Get(6);
    w2R = GokartStateTensor.Get(7);
    checkUnits();
  }

  // constructor for input stream
  public GokartState(ByteBuffer byteBuffer) {
    // assume that the input stream contains 8 floats
    // if not, IllegalArgumentException is raised
    Ux = Quantity.of(byteBuffer.getFloat(), SI.VELOCITY);
    Uy = Quantity.of(byteBuffer.getFloat(), SI.VELOCITY);
    dotPsi = Quantity.of(byteBuffer.getFloat(), SI.PER_SECOND);
    X = Quantity.of(byteBuffer.getFloat(), SI.METER);
    Y = Quantity.of(byteBuffer.getFloat(), SI.METER);
    Psi = Quantity.of(byteBuffer.getFloat(), SI.ONE);
    w2L = Quantity.of(byteBuffer.getFloat(), SI.PER_SECOND);
    w2R = Quantity.of(byteBuffer.getFloat(), SI.PER_SECOND);
  }

  @Override
  public Tensor asVector() {
    return Tensors.of(//
        Ux, //
        Uy, //
        dotPsi, //
        X, //
        Y, //
        Psi, //
        w2L, //
        w2R);
  }

  boolean checkUnits() {
    Magnitude.VELOCITY.apply(Ux);
    // TODO: check this stuff
    return true;
  }

  @Override
  public void insert(ByteBuffer byteBuffer) {
    byteBuffer.putFloat(Magnitude.VELOCITY.toFloat(Ux));
    byteBuffer.putFloat(Magnitude.VELOCITY.toFloat(Uy));
    byteBuffer.putFloat(Magnitude.PER_SECOND.toFloat(dotPsi));
    byteBuffer.putFloat(Magnitude.METER.toFloat(X));
    byteBuffer.putFloat(Magnitude.METER.toFloat(Y));
    byteBuffer.putFloat(Magnitude.ONE.toFloat(Psi));
    byteBuffer.putFloat(Magnitude.PER_SECOND.toFloat(w2L));
    byteBuffer.putFloat(Magnitude.PER_SECOND.toFloat(w2R));
  }

  @Override
  public int length() {
    return 8 * 4;
  }
}
