// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.util.data.OfflineVectorInterface;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ public class GokartState implements OfflineVectorInterface, MPCNativeInsertable {
  // TODO full documentation
  // not used yet:
  // private static final Unit SCE = SteerPutEvent.UNIT_ENCODER;
  /** time in seconds from synchronized time point */
  private final float time;
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
  /** brake temperature */
  private final float bTemp;

  /** create GokartState
   * 
   * @param time time in "s"
   * @param Ux forward velocity in "m/s"
   * @param Uy lateral velocity (left is positive) in "m/s"
   * @param dotPsi rotation velicity in "1/s"
   * @param X X-position in "m"
   * @param Y Y-position in "m"
   * @param Psi orientation in "1"
   * @param w2L left rear wheelspeed in "1/s"
   * @param w2R right rear wheelspeed in "1/s"
   * @param s wheel encoder position in "CSE" */
  public GokartState(//
      float time, //
      float Ux, //
      float Uy, //
      float dotPsi, //
      float X, //
      float Y, //
      float Psi, //
      float w2L, //
      float w2R, //
      float s) {
    this.time = time;
    this.Ux = Ux;
    this.Uy = Uy;
    this.dotPsi = dotPsi;
    this.X = X;
    this.Y = Y;
    this.Psi = Psi;
    this.w2L = w2L;
    this.w2R = w2R;
    this.s = s;
    this.bTemp = 0;
  }

  /** create GokartState
   * 
   * @param time time in "s"
   * @param Ux forward velocity in "m/s"
   * @param Uy lateral velocity (left is positive) in "m/s"
   * @param dotPsi rotation velicity in "1/s"
   * @param X X-position in "m"
   * @param Y Y-position in "m"
   * @param Psi orientation in "1"
   * @param w2L left rear wheelspeed in "1/s"
   * @param w2R right rear wheelspeed in "1/s"
   * @param s wheel encoder position in "CSE"
   * @param bTemp brake temperature in "°C" */
  public GokartState(//
      float time, //
      float Ux, //
      float Uy, //
      float dotPsi, //
      float X, //
      float Y, //
      float Psi, //
      float w2L, //
      float w2R, //
      float s, //
      float bTemp) {
    this.time = time;
    this.Ux = Ux;
    this.Uy = Uy;
    this.dotPsi = dotPsi;
    this.X = X;
    this.Y = Y;
    this.Psi = Psi;
    this.w2L = w2L;
    this.w2R = w2R;
    this.s = s;
    this.bTemp = bTemp;
  }

  /** create GokartState
   * 
   * @param time time in "s"
   * @param Ux forward velocity in "m/s"
   * @param Uy lateral velocity (left is positive) in "m/s"
   * @param dotPsi rotation velicity in "1/s"
   * @param X X-position in "m"
   * @param Y Y-position in "m"
   * @param Psi orientation in "1"
   * @param w2L left rear wheelspeed in "1/s"
   * @param w2R right rear wheelspeed in "1/s"
   * @param s wheel encoder position in "CSE" */
  public GokartState(//
      Scalar time, //
      Scalar Ux, //
      Scalar Uy, //
      Scalar dotPsi, //
      Scalar X, //
      Scalar Y, //
      Scalar Psi, //
      Scalar w2L, //
      Scalar w2R, //
      Scalar s) {
    this.time = Magnitude.SECOND.toFloat(time);
    this.Ux = Magnitude.VELOCITY.toFloat(Ux);
    this.Uy = Magnitude.VELOCITY.toFloat(Uy);
    this.dotPsi = Magnitude.PER_SECOND.toFloat(dotPsi);
    this.X = Magnitude.METER.toFloat(X);
    this.Y = Magnitude.METER.toFloat(Y);
    this.Psi = Magnitude.ONE.toFloat(Psi);
    this.w2L = Magnitude.PER_SECOND.toFloat(w2L);
    this.w2R = Magnitude.PER_SECOND.toFloat(w2R);
    this.s = SteerPutEvent.ENCODER.apply(s).number().floatValue();
    this.bTemp = 0;
  }

  /** create GokartState
   * 
   * @param time time in "s"
   * @param Ux forward velocity in "m/s"
   * @param Uy lateral velocity (left is positive) in "m/s"
   * @param dotPsi rotation velicity in "1/s"
   * @param X X-position in "m"
   * @param Y Y-position in "m"
   * @param Psi orientation in "1"
   * @param w2L left rear wheelspeed in "1/s"
   * @param w2R right rear wheelspeed in "1/s"
   * @param s wheel encoder position in "CSE"
   * @param bTemp brake temperature in "°C" */
  public GokartState(//
      Scalar time, //
      Scalar Ux, //
      Scalar Uy, //
      Scalar dotPsi, //
      Scalar X, //
      Scalar Y, //
      Scalar Psi, //
      Scalar w2L, //
      Scalar w2R, //
      Scalar s, //
      Scalar bTemp) {
    this.time = Magnitude.SECOND.toFloat(time);
    this.Ux = Magnitude.VELOCITY.toFloat(Ux);
    this.Uy = Magnitude.VELOCITY.toFloat(Uy);
    this.dotPsi = Magnitude.PER_SECOND.toFloat(dotPsi);
    this.X = Magnitude.METER.toFloat(X);
    this.Y = Magnitude.METER.toFloat(Y);
    this.Psi = Magnitude.ONE.toFloat(Psi);
    this.w2L = Magnitude.PER_SECOND.toFloat(w2L);
    this.w2R = Magnitude.PER_SECOND.toFloat(w2R);
    this.s = SteerPutEvent.ENCODER.apply(s).number().floatValue();
    this.bTemp = Magnitude.DEGREE_CELSIUS.toFloat(bTemp);
  }

  /** create GokartState
   * 
   * @param GokartStateTensor the tensor in the form:
   * {time [s],
   * Ux [m/s],
   * Uy [m/s],
   * dotPsi [1/s],
   * X [m],
   * Y [m],
   * Psi [1],
   * w2L [1/s],
   * w2R [1/s],
   * s [CSE],
   * bTemp [°C]} */
  public GokartState(Tensor GokartStateTensor) {
    // TODO reuse constructors
    // this(time, Ux, Uy, dotPsi, Ux, Uy, dotPsi, w2L, w2R, Psi)
    time = Magnitude.SECOND.toFloat(GokartStateTensor.Get(0));
    Ux = Magnitude.VELOCITY.toFloat(GokartStateTensor.Get(1));
    Uy = Magnitude.VELOCITY.toFloat(GokartStateTensor.Get(2));
    dotPsi = Magnitude.PER_SECOND.toFloat(GokartStateTensor.Get(3));
    X = Magnitude.METER.toFloat(GokartStateTensor.Get(4));
    Y = Magnitude.METER.toFloat(GokartStateTensor.Get(5));
    Psi = Magnitude.ONE.toFloat(GokartStateTensor.Get(6));
    w2L = Magnitude.PER_SECOND.toFloat(GokartStateTensor.Get(7));
    w2R = Magnitude.PER_SECOND.toFloat(GokartStateTensor.Get(8));
    s = SteerPutEvent.ENCODER.apply(GokartStateTensor.Get(9)).number().floatValue();
    bTemp = Magnitude.DEGREE_CELSIUS.toFloat(GokartStateTensor.Get(10));
  }

  // constructor for input stream
  public GokartState(ByteBuffer byteBuffer) {
    // assume that the input stream contains 8 floats
    // if not, IllegalArgumentException is raised
    time = byteBuffer.getFloat();
    Ux = byteBuffer.getFloat();
    Uy = byteBuffer.getFloat();
    dotPsi = byteBuffer.getFloat();
    X = byteBuffer.getFloat();
    Y = byteBuffer.getFloat();
    Psi = byteBuffer.getFloat();
    w2L = byteBuffer.getFloat();
    w2R = byteBuffer.getFloat();
    s = byteBuffer.getFloat();
    bTemp = byteBuffer.getFloat();
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
    return Tensors.of( //
        getTime(), //
        getUx(), //
        getUy(), //
        getdotPsi(), //
        getX(), //
        getY(), //
        getPsi(), //
        getw2L(), //
        getw2R(), //
        getS(), //
        getBTemp());
  }

  public Scalar getTime() {
    return Quantity.of(time, SI.SECOND);
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
    return RealScalar.of(Psi);
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

  public Scalar getBTemp() {
    return Quantity.of(bTemp, NonSI.DEGREE_CELSIUS);
  }

  @Override
  public void insert(ByteBuffer byteBuffer) {
    byteBuffer.putFloat(time); // 0
    byteBuffer.putFloat(Ux); // 4
    byteBuffer.putFloat(Uy); // 8
    byteBuffer.putFloat(dotPsi); // 12
    byteBuffer.putFloat(X); // 16
    byteBuffer.putFloat(Y); // 20
    byteBuffer.putFloat(Psi); // 24
    byteBuffer.putFloat(w2L); // 28
    byteBuffer.putFloat(w2R); // 32
    byteBuffer.putFloat(s); // 36
    byteBuffer.putFloat(bTemp); // 40
  }

  @Override
  public int length() {
    return 11 * Float.BYTES; // 11 * 4
  }

  @Override
  public String toString() {
    return "State:\n" + asVector().toString() + "\n";
  }
}
