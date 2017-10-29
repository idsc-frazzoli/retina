// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.util.Optional;

import ch.ethz.idsc.owly.car.math.DifferentialSpeed;
import ch.ethz.idsc.owly.data.TimeKeeper;
import ch.ethz.idsc.owly.demo.rice.Rice1StateSpaceModel;
import ch.ethz.idsc.owly.math.flow.MidpointIntegrator;
import ch.ethz.idsc.owly.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owly.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owly.math.state.StateTime;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.retina.dev.steer.SteerAngleTracker;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.qty.Quantity;

/** position control for steering
 * differential speed on rear wheels according to steering angle
 * 
 * TODO NRJ still uses velocity control for RIMO */
public class HmiSimpleDriveJoystick extends HmiAbstractJoystick {
  private static final Scalar AXIS_DELTA = Quantity.of(1.2, "m");
  private static final Scalar TIRE_L = Quantity.of(+0.54, "m");
  private static final Scalar TIRE_R = Quantity.of(-0.54, "m");
  // ---
  private final TimeKeeper timeKeeper = new TimeKeeper();
  private final DifferentialSpeed dsL = new DifferentialSpeed(AXIS_DELTA, TIRE_L);
  private final DifferentialSpeed dsR = new DifferentialSpeed(AXIS_DELTA, TIRE_R);
  private final EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
      Rice1StateSpaceModel.of(RealScalar.ZERO), //
      MidpointIntegrator.INSTANCE, //
      new StateTime(Array.zeros(1), RealScalar.ZERO));

  @Override
  protected double breakStrength() {
    return Math.max( //
        _joystick.getLeftSliderUnitValue(), //
        _joystick.getRightSliderUnitValue());
  }

  /** tire speed */
  private final RimoPutProvider rimoPutProvider = new RimoPutProvider() {
    @Override
    public Optional<RimoPutEvent> putEvent() {
      final Scalar now = timeKeeper.now();
      Scalar push = RealScalar.ZERO;
      if (hasJoystick())
        push = RealScalar.of(_joystick.getLeftKnobDirectionUp() * getSpeedLimit());
      // FIXME use increments
      episodeIntegrator.move(Tensors.of(push), now);
      if (hasJoystick()) {
        // GenericXboxPadJoystick joystick = _joystick;
        final SteerAngleTracker steerAngleTracker = SteerSocket.INSTANCE.getSteerAngleTracker();
        if (steerAngleTracker.isCalibrated()) {
          StateTime rate = episodeIntegrator.tail();
          Scalar speed = rate.state().Get(0);
          Scalar theta = RealScalar.of(steerAngleTracker.getSteeringValue());
          Scalar sL = dsL.get(speed, theta);
          Scalar sR = dsR.get(speed, theta);
          return Optional.of(RimoPutEvent.withSpeeds( //
              sL.number().shortValue(), //
              sR.number().shortValue()));
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
