// code by jph
package ch.ethz.idsc.gokart.lcm.autobox;

import ch.ethz.idsc.gokart.dev.misc.MiscGetEvent;
import ch.ethz.idsc.gokart.dev.misc.MiscGetListener;
import ch.ethz.idsc.gokart.dev.misc.MiscPutEvent;
import ch.ethz.idsc.gokart.dev.misc.MiscPutListener;
import ch.ethz.idsc.gokart.lcm.BinaryBlobPublisher;

public enum MiscLcmServer implements MiscGetListener, MiscPutListener {
  INSTANCE;
  // ---
  public static final String CHANNEL_GET = "autobox.misc.get";
  public static final String CHANNEL_PUT = "autobox.misc.put";
  // ---
  private final BinaryBlobPublisher getPublisher = new BinaryBlobPublisher(CHANNEL_GET);
  private final BinaryBlobPublisher putPublisher = new BinaryBlobPublisher(CHANNEL_PUT);

  @Override
  public void getEvent(MiscGetEvent miscGetEvent) {
    getPublisher.accept(miscGetEvent.asArray());
  }

  @Override
  public void putEvent(MiscPutEvent miscPutEvent) {
    putPublisher.accept(miscPutEvent.asArray());
  }
}
