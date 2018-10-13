// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.util.data.OfflineVectorInterface;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.qty.Unit;

/* package */ class GokartState implements OfflineVectorInterface, MPCNativeInsertable {
  private static final Unit SCE = SteerPutEvent.UNIT_ENCODER;
  /** forward velocity in gokart frame with unit m*s^1 */
  private final float Ux;
  /** sidewards velocity in gokart frame with unit m*s^1 */
  private final float Uy;
  /** angular velocity with unit rad*s^-1 */
  private final float dotPsi;
  /** global position in X direction with unit m */
  private final float X;
  /** global position in Y direction with unit m */
  private final float Y;
  /** orientation with unit rad */
  private final float Psi;
  /** left rear wheel speed */
  private final float w2L;
  /** right rear wheel speed */
  private final float w2R;
  /** steering state */
  private final float s;

  public GokartState(//
      float Ux, //
      float Uy, //
      float dotPsi, //
      float X, //
      float Y, //
      float Psi, //
      float w2L, //
      float w2R, float s) {
    this.Ux = Ux;
    this.Uy = Uy;
    this.dotPsi = dotPsi;
    this.X = X;
    this.Y = Y;
    this.Psi = Psi;
    this.w2L = w2L;
    this.w2R = w2R;
    this.s = s;
  }
  

  public GokartState(//
      Scalar Ux, //
      Scalar Uy, //
      Scalar dotPsi, //
      Scalar X, //
      Scalar Y, //
      Scalar Psi, //
      Scalar w2L, //
      Scalar w2R, //
      Scalar s) {
    this.Ux = Magnitude.VELOCITY.toFloat(Ux);
    this.Uy = Magnitude.VELOCITY.toFloat(Uy);
    this.dotPsi = Magnitude.PER_SECOND.toFloat(dotPsi);
    this.X = Magnitude.METER.toFloat(X);
    this.Y = Magnitude.METER.toFloat(Y);
    this.Psi = Magnitude.ONE.toFloat(Psi);
    this.w2L = Magnitude.PER_SECOND.toFloat(w2L);
    this.w2R = Magnitude.PER_SECOND.toFloat(w2R);
    this.s = SteerPutEvent.ENCODER.apply(s).number().floatValue();
  }

  public GokartState(Tensor GokartStateTensor) {
    Ux = Magnitude.VELOCITY.toFloat(GokartStateTensor.Get(0));
    Uy = Magnitude.VELOCITY.toFloat(GokartStateTensor.Get(1));
    dotPsi = Magnitude.PER_SECOND.toFloat(GokartStateTensor.Get(2));
    X = Magnitude.METER.toFloat(GokartStateTensor.Get(3));
    Y = Magnitude.METER.toFloat(GokartStateTensor.Get(4));
    Psi = Magnitude.ONE.toFloat(GokartStateTensor.Get(5));
    w2L = Magnitude.PER_SECOND.toFloat(GokartStateTensor.Get(6));
    w2R = Magnitude.PER_SECOND.toFloat(GokartStateTensor.Get(7));
    s = SteerPutEvent.ENCODER.apply(GokartStateTensor.Get(8)).number().floatValue();
  }

  // constructor for input stream
  public GokartState(ByteBuffer byteBuffer) {
    // assume that the input stream contains 8 floats
    // if not, IllegalArgumentException is raised
    Ux = byteBuffer.getFloat();
    Uy = byteBuffer.getFloat();
    dotPsi = byteBuffer.getFloat();
    X = byteBuffer.getFloat();
    Y = byteBuffer.getFloat();
    Psi = byteBuffer.getFloat();
    w2L = byteBuffer.getFloat();
    w2R = byteBuffer.getFloat();
    s = byteBuffer.getFloat();
  }

  @Override
  public Tensor asVector() {
    /* return Tensors.vector(//
     * Ux, //
     * Uy, //
     * dotPsi, //
     * X, //
     * Y, //
     * Psi, //
     * w2L, //
     * w2R,
     * s); */
    return Tensors.of(//
        getUx(),//
        getUy(),
        getdotPsi(),//
        getX(),//
        getY(),//
        getPsi(),//
        getw2L(),//
        getw2R(),//
        getS());
  }

  public Scalar getUx() {
    return Quantity.of(Ux, SI.VELOCITY);
  }

  public Scalar getUy() {
    return Quantity.of(Uy, SI.VELOCITY);
  }

  public Scalar getdotPsi() {
    return Quantity.of(dotPsi, SI.PER_SECOND);
  }

  public Scalar getX() {
    return Quantity.of(X, SI.METER);
  }

  public Scalar getY() {
    return Quantity.of(Y, SI.METER);
  }

  public Scalar getPsi() {
    return Quantity.of(Psi, SI.ONE);
  }

  public Scalar getw2L() {
    return Quantity.of(w2L, SI.PER_SECOND);
  }

  public Scalar getw2R() {
    return Quantity.of(w2R, SI.PER_SECOND);
  }

  public Scalar getS() {
    return Quantity.of(s, SteerPutEvent.UNIT_ENCODER);
  }

  @Override
  public void insert(ByteBuffer byteBuffer) {
    byteBuffer.putFloat(Ux);
    byteBuffer.putFloat(Uy);
    byteBuffer.putFloat(dotPsi);
    byteBuffer.putFloat(X);
    byteBuffer.putFloat(Y);
    byteBuffer.putFloat(Psi);
    byteBuffer.putFloat(w2L);
    byteBuffer.putFloat(w2R);
    byteBuffer.putFloat(s);
  }

  @Override
  public int length() {
    return 9 * 4;
  }
  
  public String toString(){
    return "State:\n"+asVector().toString()+"\n";
  }
}
