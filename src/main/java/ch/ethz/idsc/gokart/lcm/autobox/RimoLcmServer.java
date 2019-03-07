// code by jph
package ch.ethz.idsc.gokart.lcm.autobox;

import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetListener;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutListener;
import ch.ethz.idsc.gokart.lcm.BinaryBlobPublisher;

public enum RimoLcmServer implements RimoGetListener, RimoPutListener {
  INSTANCE;
  // ---
  public static final String CHANNEL_GET = "autobox.rimo.get";
  public static final String CHANNEL_PUT = "autobox.rimo.put";
  // ---
  private final BinaryBlobPublisher getPublisher = new BinaryBlobPublisher(CHANNEL_GET);
  private final BinaryBlobPublisher putPublisher = new BinaryBlobPublisher(CHANNEL_PUT);

  @Override // from RimoGetListener
  public void getEvent(RimoGetEvent rimoGetEvent) {
    getPublisher.accept(rimoGetEvent.asArray());
  }

  @Override // from RimoPutListener
  public void putEvent(RimoPutEvent rimoPutEvent) {
    putPublisher.accept(rimoPutEvent.asArray());
  }
}
