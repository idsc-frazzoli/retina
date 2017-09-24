// code by jph
package ch.ethz.idsc.retina.lcm.autobox;

import ch.ethz.idsc.retina.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutListener;
import ch.ethz.idsc.retina.lcm.BinaryBlobPublisher;

public enum LinmotLcmServer implements LinmotGetListener, LinmotPutListener {
  INSTANCE;
  // ---
  private final BinaryBlobPublisher getPublisher = new BinaryBlobPublisher("autobox.linmot.get");
  private final BinaryBlobPublisher putPublisher = new BinaryBlobPublisher("autobox.linmot.put");

  @Override
  public void getEvent(LinmotGetEvent linmotGetEvent) {
    getPublisher.accept(linmotGetEvent.asArray());
  }

  @Override
  public void putEvent(LinmotPutEvent linmotPutEvent) {
    putPublisher.accept(linmotPutEvent.asArray());
  }
}
