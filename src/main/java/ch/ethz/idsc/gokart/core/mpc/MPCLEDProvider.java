// code by mh, ta
package ch.ethz.idsc.gokart.core.mpc;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.calib.steer.HighPowerSteerPid;
import ch.ethz.idsc.gokart.calib.steer.SteerFeedForward;
import ch.ethz.idsc.gokart.core.fuse.Vlp16PassiveSlowing;
import ch.ethz.idsc.gokart.dev.led.LEDPutEvent;
import ch.ethz.idsc.gokart.dev.led.LEDPutProvider;
import ch.ethz.idsc.gokart.dev.led.LEDStatus;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.gokart.dev.steer.SteerPositionControl;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.led.LEDLcm;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ final class MPCLEDProvider implements LEDPutProvider {
  // ---
  private final SteerColumnInterface steerColumnInterface = SteerSocket.INSTANCE.getSteerColumnTracker();
  final Timing timing;
  private final MPCSteering mpcSteering;
  private final MpcLedFunction mpcLedFunction = MpcLedFunction.DETAILED;

  public MPCLEDProvider(Timing timing, MPCSteering mpcSteering) {
    this.mpcSteering = mpcSteering;
    this.timing = timing;
  }

  @Override // from PutProvider
  public Optional<LEDPutEvent> putEvent() {
    Scalar time = Quantity.of(timing.seconds(), SI.SECOND);
    return mpcSteering.getSteering(time).map(this::angleLED); // use steering angle
  }

  private LEDPutEvent angleLED(Tensor steering) {
    Scalar currAngle = steerColumnInterface.getSteerColumnEncoderCentered();
    return LEDPutEvent.from(mpcLedFunction.apply(steering.Get(0), currAngle));
  }

  public final ProviderRank getProviderRank() {
    return ProviderRank.AUTONOMOUS;
  }
}
