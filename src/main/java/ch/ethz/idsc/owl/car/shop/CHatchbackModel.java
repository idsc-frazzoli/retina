// code by edo
// code adapted by jph
package ch.ethz.idsc.owl.car.shop;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.owl.car.core.VehicleModel;
import ch.ethz.idsc.owl.car.core.WheelInterface;
import ch.ethz.idsc.owl.car.math.Pacejka3;
import ch.ethz.idsc.owl.car.model.CarControl;
import ch.ethz.idsc.owl.car.model.CarSteering;
import ch.ethz.idsc.owl.car.model.DefaultCarModel;
import ch.ethz.idsc.owl.car.model.DefaultWheel;
import ch.ethz.idsc.owl.car.model.MotorTorques;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.sca.Clip;

public class CHatchbackModel extends DefaultCarModel {
  private static final Scalar WIDTH_165 = RealScalar.of(0.165);

  public static VehicleModel standard() {
    return new CHatchbackModel(CarSteering.FRONT, RealScalar.ZERO);
  }

  // ---
  private final CarSteering carSteering;
  private final Scalar gammaM;
  private final List<WheelInterface> list = new ArrayList<>();

  /** @param carSteering
   * @param gammaM rear/total drive ratio; 0 is FWD, 1 is RWD, 0.5 is AWD */
  public CHatchbackModel(CarSteering carSteering, Scalar gammaM) {
    this.carSteering = carSteering;
    this.gammaM = gammaM;
    final Scalar radius = DoubleScalar.of(0.325); // wheel radius [m]
    final Scalar IW = DoubleScalar.of(0.9); // wheel inertia [kgm2]
    final Pacejka3 PACEJKA1 = new Pacejka3(13.8509, 1.3670, 0.9622);
    final Pacejka3 PACEJKA2 = new Pacejka3(14.1663, 1.3652, 0.9744);
    final Scalar LW = DoubleScalar.of(0.8375); // lateral distance of wheels from COG [m]
    final Scalar LF = DoubleScalar.of(1.015); // front axle distance from COG [m]
    final Scalar LR = DoubleScalar.of(1.895); // rear axle distance from COG [m]
    final Scalar LZ = DoubleScalar.of(-0.54); // from COG to ground contact level [m]
    list.add(new DefaultWheel(radius, WIDTH_165, IW, PACEJKA1, Tensors.of(LF, LW, LZ)));
    list.add(new DefaultWheel(radius, WIDTH_165, IW, PACEJKA1, Tensors.of(LF, LW.negate(), LZ)));
    list.add(new DefaultWheel(radius, WIDTH_165, IW, PACEJKA2, Tensors.of(LR.negate(), LW, LZ)));
    list.add(new DefaultWheel(radius, WIDTH_165, IW, PACEJKA2, Tensors.of(LR.negate(), LW.negate(), LZ)));
  }

  @Override
  public WheelInterface wheel(int index) {
    return list.get(index);
  }

  // ---
  @Override
  public Scalar mass() {
    return DoubleScalar.of(1412); // mass [kg]
  }

  @Override
  public Tensor footprint() {
    double xf = 1.915; // from COG to front end [m]
    double xr = -2.835; // from COG to rear end [m]
    double yw = 1.916 * 0.5; // width of the vehicle [m]
    return Tensors.matrixDouble(new double[][] { //
        { xf, yw }, //
        { xr, yw }, //
        { xr, -yw }, //
        { xf, -yw } //
    });
  }

  @Override
  public Scalar Iz_invert() {
    return DoubleScalar.of(1 / (1536.7 + 427.7084)); // yawing moment of inertia [kgm2]
  }

  @Override
  public Scalar b() {
    return DoubleScalar.of(5); // dynamic friction coefficient N/(m/s)
  }

  @Override
  public Scalar fric() {
    return DoubleScalar.of(47); // coulomb friction
  }

  @Override
  public CarSteering steering() {
    return carSteering;
  }

  // maximal steering angle [deg]
  // check online what is appropriate
  private static final Scalar MAX_DELTA = DoubleScalar.of(45 * Math.PI / 180);
  // maximal motor torque [Nm], with gears included
  private static final Scalar MAX_PRESS = DoubleScalar.of(13); // maximal master cylinder presure [MPa]
  private static final Scalar MAX_THB = DoubleScalar.of(2000); // max handbrake torque [Nm]
  private static final Scalar MAX_THROTTLE = DoubleScalar.of(2000.);

  @Override
  public CarControl createControl(Tensor u) {
    Clip.absoluteOne().requireInside(u.Get(0));
    Clip.unit().requireInside(u.Get(3));
    // ---
    Scalar delta = u.Get(0).multiply(MAX_DELTA).multiply(carSteering.factor);
    Scalar brake = u.Get(1).multiply(MAX_PRESS);
    Scalar handbrake = u.Get(2).multiply(MAX_THB);
    Scalar throttle = u.Get(3).multiply(MAX_THROTTLE);
    return new CarControl(delta, brake, handbrake, MotorTorques.standard(gammaM, throttle));
  }

  @Override
  public Scalar press2torF() {
    return DoubleScalar.of(250); // Nm per Mpa conversion constant [Nm/Mpa] for Front and Rear brakes
  }

  @Override
  public Scalar press2torR() {
    return DoubleScalar.of(150);
  }

  @Override
  public Scalar muRoll() {
    // for ==2 the car will not make a turn but slide in nose direction...
    return DoubleScalar.of(0); // rolling friction coefficient
  }
}
