// code by mcp (used PurePursuite by jph as model)
package ch.ethz.idsc.demo.mp.pid;

import java.util.Optional;

import ch.ethz.idsc.gokart.calib.steer.SteerMapping;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.gokart.dev.steer.SteerPositionControl;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerPutProvider;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ class PIDSteer implements SteerPutProvider, StartAndStoppable {
  private final SteerColumnInterface steerColumnInterface = SteerSocket.INSTANCE.getSteerColumnTracker();
  private final SteerMapping steerMapping = SteerConfig.GLOBAL.getSteerMapping();
  private final SteerPositionControl steerPositionController = new SteerPositionControl();

  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.AUTONOMOUS;
  }

  @Override // from SteerPutProvider
  public Optional<SteerPutEvent> putEvent() {
    if (steerColumnInterface.isSteerColumnCalibrated()) {
      Scalar currAngle = steerColumnInterface.getSteerColumnEncoderCentered();
      Scalar desPos = steerMapping.getSCEfromRatio(ratio);
      Scalar difference = desPos.subtract(currAngle);
      Scalar torqueCmd = steerPositionController.iterate(difference);
      return Optional.of(SteerPutEvent.createOn(torqueCmd));
    }
    return Optional.empty();
  }

  @Override // from StartAndStoppable
  public void start() {
    SteerSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from StartAndStoppable
  public void stop() {
    SteerSocket.INSTANCE.removePutProvider(this);
  }

  private Scalar ratio = Quantity.of(0.0, SI.PER_METER);

  /* package */ void setRatio(Scalar ratio) {
    this.ratio = ratio;
  }

  /* package */ Scalar getRatio() {
    return ratio;
  }
}
