// code by jph
package ch.ethz.idsc.retina.lcm.autobox;

import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetListener;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutListener;
import ch.ethz.idsc.retina.lcm.BinaryBlobPublisher;

public enum RimoLcmServer implements RimoGetListener, RimoPutListener {
  INSTANCE;
  // ---
  private final BinaryBlobPublisher getPublisher = new BinaryBlobPublisher("autobox.rimo.get");
  private final BinaryBlobPublisher putPublisher = new BinaryBlobPublisher("autobox.rimo.put");

  @Override
  public void getEvent(RimoGetEvent rimoGetEvent) {
    getPublisher.accept(rimoGetEvent.asArray());
  }

  @Override
  public void putEvent(RimoPutEvent rimoPutEvent) {
    putPublisher.accept(rimoPutEvent.asArray());
  }
}
