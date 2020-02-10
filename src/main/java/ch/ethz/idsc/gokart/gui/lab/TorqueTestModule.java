// code by gjoel
package ch.ethz.idsc.gokart.gui.lab;

import java.util.Objects;
import java.util.Optional;

import ch.ethz.idsc.gokart.calib.steer.SteerColumnEvent;
import ch.ethz.idsc.gokart.calib.steer.SteerColumnListener;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerPutProvider;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.gokart.lcm.autobox.SteerColumnLcmClient;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractClockedModule;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;
import ch.ethz.idsc.tensor.sca.Sign;

public class TorqueTestModule extends AbstractClockedModule implements SteerColumnListener, SteerPutProvider {
  private static final Clip RANGE = Clips.interval( //
      Quantity.of(-0.5, SteerPutEvent.UNIT_ENCODER), //
      Quantity.of(0.5, SteerPutEvent.UNIT_ENCODER));
  private static final Scalar INCR = Quantity.of(0.1, SteerPutEvent.UNIT_RTORQUE);
  // ---
  private final SteerColumnLcmClient steerColumnLcmClient = new SteerColumnLcmClient();
  // ---
  private boolean crossing = false;
  private Scalar trq = SteerConfig.GLOBAL.calibration;
  private SteerColumnEvent steerColumnEvent = null;
  private SteerPutEvent steerPutEvent = null;

  @Override // from AbstractModule
  public void first() {
    steerColumnLcmClient.addListener(this);
    steerColumnLcmClient.startSubscriptions();
    // ---
    SteerSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from AbstractModule
  public void last() {
    SteerSocket.INSTANCE.removePutProvider(this);
    // ---
    steerColumnLcmClient.stopSubscriptions();
  }

  @Override // from SteerColumnListener
  public void getEvent(SteerColumnEvent getEvent) {
    steerColumnEvent = getEvent;
  }

  @Override // from AbstractClockedModule
  public Scalar getPeriod() {
    return Quantity.of(0.1, SI.SECOND);
  }

  @Override // from PutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.TESTING;
  }

  @Override // from PutProvider
  public Optional<SteerPutEvent> putEvent() {
    return Optional.ofNullable(steerPutEvent);
  }

  @Override // from AbstractClockedModule
  public void runAlgo() {
    if (Objects.nonNull(steerColumnEvent))
      if (SteerSocket.INSTANCE.getSteerColumnTracker().isCalibratedAndHealthy()) {
        if (RANGE.isOutside(steerColumnEvent.getSteerColumnEncoderCentered())) {
          if (crossing)
            stepTorque();
          crossing = false;
        } else
          crossing = true;
        steerPutEvent = SteerPutEvent.createOn(trq);
      } else {
        System.err.println("Steering is not calibrated/healthy!");
        steerPutEvent = SteerPutEvent.PASSIVE_MOT_TRQ_0;
      }
  }

  private void stepTorque() {
    boolean wasPositive = Sign.isPositive(trq);
    Scalar trqAbs = trq.abs().add(INCR);
    System.out.println("current torque: +/-" + trqAbs);
    trq = wasPositive ? trqAbs.negate() : trqAbs;
  }
}
