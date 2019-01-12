// code by jph
package ch.ethz.idsc.gokart.lcm.autobox;

import ch.ethz.idsc.gokart.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.gokart.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.gokart.dev.linmot.LinmotPutListener;
import ch.ethz.idsc.gokart.lcm.BinaryBlobPublisher;

public enum LinmotLcmServer implements LinmotGetListener, LinmotPutListener {
  INSTANCE;
  // ---
  public static final String CHANNEL_GET = "autobox.linmot.get";
  public static final String CHANNEL_PUT = "autobox.linmot.put";
  // ---
  private final BinaryBlobPublisher getPublisher = new BinaryBlobPublisher(CHANNEL_GET);
  private final BinaryBlobPublisher putPublisher = new BinaryBlobPublisher(CHANNEL_PUT);

  @Override
  public void getEvent(LinmotGetEvent linmotGetEvent) {
    getPublisher.accept(linmotGetEvent.asArray());
  }

  @Override
  public void putEvent(LinmotPutEvent linmotPutEvent) {
    putPublisher.accept(linmotPutEvent.asArray());
  }
}
