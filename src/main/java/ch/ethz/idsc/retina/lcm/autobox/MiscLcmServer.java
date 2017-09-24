// code by jph
package ch.ethz.idsc.retina.lcm.autobox;

import ch.ethz.idsc.retina.dev.misc.MiscGetEvent;
import ch.ethz.idsc.retina.dev.misc.MiscGetListener;
import ch.ethz.idsc.retina.dev.misc.MiscPutEvent;
import ch.ethz.idsc.retina.dev.misc.MiscPutListener;
import ch.ethz.idsc.retina.lcm.BinaryBlobPublisher;

public enum MiscLcmServer implements MiscGetListener, MiscPutListener {
  INSTANCE;
  // ---
  private final BinaryBlobPublisher getPublisher = new BinaryBlobPublisher("autobox.misc.get");
  private final BinaryBlobPublisher putPublisher = new BinaryBlobPublisher("autobox.misc.put");

  @Override
  public void getEvent(MiscGetEvent miscGetEvent) {
    getPublisher.accept(miscGetEvent.asArray());
  }

  @Override
  public void putEvent(MiscPutEvent miscPutEvent) {
    putPublisher.accept(miscPutEvent.asArray());
  }
}
