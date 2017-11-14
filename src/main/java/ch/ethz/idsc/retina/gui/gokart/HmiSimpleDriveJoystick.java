// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.util.Optional;

import ch.ethz.idsc.owly.data.TimeKeeper;
import ch.ethz.idsc.retina.dev.rimo.RimoGetTire;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/** position control for steering
 * differential speed on rear wheels according to steering angle
 * 
 * TODO NRJ still uses velocity control for RIMO */
public class HmiSimpleDriveJoystick extends HmiAbstractJoystick {
  // ---
  private final TimeKeeper timeKeeper = new TimeKeeper();
  // private final EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
  // new Duncan1StateSpaceModel(Quantity.of(1, "s^-1")), //
  // MidpointIntegrator.INSTANCE, //
  // new StateTime(Tensors.fromString("{0[rad*s^-1]}"), Quantity.of(0, "s")));

  @Override
  protected double breakStrength() {
    return Math.max( //
        _joystick.getLeftSliderUnitValue(), //
        _joystick.getRightSliderUnitValue());
  }

  /** tire speed */
  private final RimoPutProvider rimoPutProvider = new RimoPutProvider() {
    // TODO geh vom gas falls bremse gedrueckt ist
    @Override
    public Optional<RimoPutEvent> putEvent() {
      // final Scalar now = timeKeeper.now();
      Scalar speed = Quantity.of(0, RimoGetTire.UNIT_RATE);
      if (hasJoystick())
        speed = getSpeedLimit().multiply(RealScalar.of(_joystick.getLeftKnobDirectionUp()));
      // FIXME use increments, check units
      // episodeIntegrator.move(Tensors.of(speed), now);
      if (hasJoystick()) {
        // GenericXboxPadJoystick joystick = _joystick;
        // final SteerAngleTracker steerAngleTracker = SteerSocket.INSTANCE.getSteerAngleTracker();
        // if (steerAngleTracker.isCalibrated()) // FIXME
        {
          // Scalar axisDelta = ChassisGeometry.GLOBAL.xAxleDistanceMeter();
          // Scalar yTireRear = ChassisGeometry.GLOBAL.yTireRearMeter();
          // DifferentialSpeed dsL = new DifferentialSpeed(axisDelta, yTireRear);
          // DifferentialSpeed dsR = new DifferentialSpeed(axisDelta, yTireRear.negate());
          // StateTime rate = episodeIntegrator.tail();
          // Scalar speed = rate.state().Get(0);
          // Scalar theta = RealScalar.of(steerAngleTracker.getSteeringValue());
          // Scalar sL = dsL.get(speed, theta);
          // Scalar sR = dsR.get(speed, theta);
          // System.out.println(sL);
          return rimoRateControllerWrap.iterate( //
              speed, //
              speed);
        }
      }
      return Optional.empty();
    }

    @Override
    public ProviderRank getProviderRank() {
      return ProviderRank.MANUAL;
    }
  };

  @Override
  public RimoPutProvider getRimoPutProvider() {
    return rimoPutProvider;
  }
}
