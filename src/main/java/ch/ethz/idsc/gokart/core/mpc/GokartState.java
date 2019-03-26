// code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.nio.ByteBuffer;

import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.retina.util.data.BufferInsertable;
import ch.ethz.idsc.retina.util.data.OfflineVectorInterface;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.lie.AngleVector;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ class GokartState implements OfflineVectorInterface, BufferInsertable {
  public static final int LENGTH = 11 * Float.BYTES; // 11 * 4
  private static final Scalar ZERO_DEGC = Quantity.of(0.0, NonSI.DEGREE_CELSIUS);
  // ---
  private final static Scalar CENTER_OFFSET = ChassisGeometry.GLOBAL.xAxleRtoCoM;
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
  /** steering column encoder */
  private final float s;
  /** brake temperature */
  private final float bTemp;

  /** create GokartState
   * 
   * @param time in "s"
   * @param Ux forward velocity in "m/s"
   * @param Uy lateral velocity (left is positive) in "m/s"
   * @param dotPsi rotation velocity in "1/s"
   * @param X X-position in "m"
   * @param Y Y-position in "m"
   * @param Psi orientation in "1"
   * @param w2L left rear wheel speed in "1/s"
   * @param w2R right rear wheel speed in "1/s"
   * @param s wheel encoder position in "SCE" */
  GokartState(//
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
    this(time, Ux, Uy, dotPsi, X, Y, Psi, w2L, w2R, s, 0f);
  }

  /** create GokartState
   * 
   * @param time in "s"
   * @param Ux forward velocity in "m/s"
   * @param Uy lateral velocity (left is positive) in "m/s"
   * @param dotPsi rotation vel0city in "1/s"
   * @param X X-position in "m"
   * @param Y Y-position in "m"
   * @param Psi orientation in "1"
   * @param w2L left rear wheel speed in "1/s"
   * @param w2R right rear wheel speed in "1/s"
   * @param s wheel encoder position in "SCE"
   * @param bTemp brake temperature in "degC" */
  public GokartState( //
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
   * @param dotPsi rotation velocity in "1/s"
   * @param X X-position in "m"
   * @param Y Y-position in "m"
   * @param Psi orientation in "1"
   * @param w2L left rear wheel speed in "1/s"
   * @param w2R right rear wheel speed in "1/s"
   * @param s wheel encoder position in "SCE" */
  public GokartState( //
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
    this(time, Ux, Uy, dotPsi, X, Y, Psi, w2L, w2R, s, ZERO_DEGC);
  }

  /** create GokartState
   * 
   * @param time time in "s"
   * @param Ux forward velocity in "m/s"
   * @param Uy lateral velocity (left is positive) in "m/s"
   * @param dotPsi rotation velocity in "1/s"
   * @param X X-position in "m"
   * @param Y Y-position in "m"
   * @param Psi orientation in "1"
   * @param w2L left rear wheel speed in "1/s"
   * @param w2R right rear wheel speed in "1/s"
   * @param s wheel encoder position in "SCE"
   * @param bTemp brake temperature in "degC" */
  public GokartState( //
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

  /** constructor for input stream
   * assume that the input stream contains 11 floats
   * 
   * @param byteBuffer
   * @throws Exception if insufficient bytes are available */
  public GokartState(ByteBuffer byteBuffer) {
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

  /** @return quantity with unit "m" */
  public Scalar getX() {
    return Quantity.of(X, SI.METER);
  }

  /** @return quantity with unit "m" */
  public Scalar getY() {
    return Quantity.of(Y, SI.METER);
  }

  /** @return heading of vehicle with interpretation in radians */
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

  /** @return {x[m], y[m], psi} */
  public Tensor getPose() {
    return Tensors.of(getX(), getY(), getPsi());
  }

  /** @return {x[m], y[m]} */
  public Tensor getPositionXY() {
    return Tensors.of(getX(), getY());
  }

  public Tensor getCenterPosition() {
    return getPositionXY().add(AngleVector.of(getPsi()).multiply(CENTER_OFFSET));
  }

  @Override // from BufferInsertable
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

  @Override // from BufferInsertable
  public int length() {
    return LENGTH;
  }

  @Override
  public String toString() {
    return "State:\n" + asVectorWithUnits() + "\n";
  }

  @Override // from OfflineVectorInterface
  public Tensor asVector() {
    return Tensors.vector(//
        time, //
        Ux, //
        Uy, //
        dotPsi, //
        X, //
        Y, //
        Psi, //
        w2L, //
        w2R, //
        s, //
        bTemp);
  }

  Tensor asVectorWithUnits() {
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
}
