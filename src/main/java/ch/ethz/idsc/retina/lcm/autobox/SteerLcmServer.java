// code by jph
package ch.ethz.idsc.retina.lcm.autobox;

import ch.ethz.idsc.retina.dev.steer.SteerGetEvent;
import ch.ethz.idsc.retina.dev.steer.SteerGetListener;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.dev.steer.SteerPutListener;
import ch.ethz.idsc.retina.lcm.BinaryBlobPublisher;

public enum SteerLcmServer implements SteerGetListener, SteerPutListener {
  INSTANCE;
  // ---
  private final BinaryBlobPublisher getPublisher = new BinaryBlobPublisher("autobox.steer.get");
  private final BinaryBlobPublisher putPublisher = new BinaryBlobPublisher("autobox.steer.put");

  @Override
  public void getEvent(SteerGetEvent steerGetEvent) {
    getPublisher.accept(steerGetEvent.asArray());
  }

  @Override
  public void putEvent(SteerPutEvent steerPutEvent) {
    putPublisher.accept(steerPutEvent.asArray());
  }
}
