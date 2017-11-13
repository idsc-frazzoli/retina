// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetListener;
import ch.ethz.idsc.retina.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoPutProvider;
import ch.ethz.idsc.retina.dev.rimo.RimoPutTire;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.retina.sys.AbstractModule;

public class RimoMetronomeModule extends AbstractModule implements RimoPutProvider, RimoGetListener {
  @Override
  protected void first() throws Exception {
    RimoSocket.INSTANCE.addGetListener(this);
    RimoSocket.INSTANCE.addPutProvider(this);
  }

  @Override
  protected void last() {
    RimoSocket.INSTANCE.removePutProvider(this);
    RimoSocket.INSTANCE.removeGetListener(this);
  }

  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.CALIBRATION;
  }

  @Override
  public Optional<RimoPutEvent> putEvent() {
    RimoPutTire putL = new RimoPutTire(RimoPutTire.OPERATION, (short) 0, (short) 50);
    RimoPutEvent rimoPutEvent = new RimoPutEvent(putL, RimoPutTire.STOP);
    return Optional.of(rimoPutEvent);
  }

  @Override
  public void getEvent(RimoGetEvent rimoGetEvent) {
    System.out.println(rimoGetEvent);
  }
}
