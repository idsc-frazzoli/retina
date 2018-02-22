// code by jph
package ch.ethz.idsc.gokart.lcm.autobox;

import ch.ethz.idsc.retina.dev.steer.SteerGetEvent;
import ch.ethz.idsc.retina.dev.steer.SteerGetListener;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.dev.steer.SteerPutListener;
import ch.ethz.idsc.retina.lcm.BinaryBlobPublisher;

public enum SteerLcmServer implements SteerGetListener, SteerPutListener {
  INSTANCE;
  // ---
  public static final String CHANNEL_GET = "autobox.steer.get";
  public static final String CHANNEL_PUT = "autobox.steer.put";
  // ---
  private final BinaryBlobPublisher getPublisher = new BinaryBlobPublisher(CHANNEL_GET);
  private final BinaryBlobPublisher putPublisher = new BinaryBlobPublisher(CHANNEL_PUT);

  @Override
  public void getEvent(SteerGetEvent steerGetEvent) {
    getPublisher.accept(steerGetEvent.asArray());
  }

  @Override
  public void putEvent(SteerPutEvent steerPutEvent) {
    putPublisher.accept(steerPutEvent.asArray());
  }
}
