// code by jph
package ch.ethz.idsc.gokart.core.joy;

import java.util.Optional;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrameListener;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutHelper;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.lcm.davis.DavisImuLcmClient;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.qty.Quantity;

public class TorqueVectoringJoystickModule extends GuideJoystickModule<RimoPutEvent> //
    implements DavisImuFrameListener {
  private final DavisImuLcmClient davisImuLcmClient = new DavisImuLcmClient(GokartLcmChannel.DAVIS_OVERVIEW);
  private Scalar gyro_Z = Quantity.of(0, SI.PER_SECOND);

  @Override // from AbstractModule
  void protected_first() {
    davisImuLcmClient.addListener(this);
    davisImuLcmClient.startSubscriptions();
    RimoSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from AbstractModule
  void protected_last() {
    RimoSocket.INSTANCE.removePutProvider(this);
    davisImuLcmClient.stopSubscriptions();
  }

  /***************************************************/
  @Override // from GuideJoystickModule
  Optional<RimoPutEvent> control( //
      SteerColumnInterface steerColumnInterface, GokartJoystickInterface joystick) {
    Scalar theta = SteerConfig.GLOBAL.getAngleFromSCE(steerColumnInterface); // steering angle
    Scalar pair = Differences.of(joystick.getAheadPair_Unit()).Get(0);
    // gyro_Z
    // TorqueVectoringConfig.GLOBAL.brakeDuration
    // Scalar pair = joystick.getAheadPair_Unit().Get(1); // entry in [0, 1]
    pair = pair.multiply(JoystickConfig.GLOBAL.torqueLimit);
    short arms_raw = Magnitude.ARMS.toShort(pair); // confirm that units are correct
    return Optional.of(RimoPutHelper.operationTorque( //
        (short) -arms_raw, // sign left invert
        (short) +arms_raw // sign right id
    ));
  }

  @Override
  public void imuFrame(DavisImuFrame davisImuFrame) {
    gyro_Z = davisImuFrame.gyroImageFrame().Get(1); // TODO magic const
  }
}
