package ch.ethz.idsc.retina.gui.gokart;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutConfiguration;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutListener;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutProvider;
import ch.ethz.idsc.retina.dev.linmot.LinmotSocket;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.retina.sys.AbstractModule;

public class LinmotTakeoverModule extends AbstractModule implements LinmotGetListener, LinmotPutListener, LinmotPutProvider {
  @Override
  protected void first() throws Exception {
    LinmotSocket.INSTANCE.addAll(this);
  }

  private boolean isActive = false;
  private boolean takeover = false;

  @Override
  protected void last() {
    LinmotSocket.INSTANCE.removeAll(this);
  }

  @Override
  public void getEvent(LinmotGetEvent getEvent) {
    // TODO Auto-generated method stub
    int difference = getEvent.demand_position - getEvent.actual_position;
    System.out.println(difference);
    if (isActive && difference >= 20000) {
      takeover = true;
    }
  }

  @Override
  public ProviderRank getProviderRank() {
    return ProviderRank.EMERGENCY;
  }

  @Override
  public Optional<LinmotPutEvent> putEvent() {
    // TODO Auto-generated method stub
    if (takeover) {
      LinmotPutEvent takeOverEvent = new LinmotPutEvent(LinmotPutConfiguration.CMD_OFF_MODE, LinmotPutConfiguration.MC_ZEROS);
      return Optional.of(takeOverEvent);
    }
    return Optional.empty();
  }

  @Override
  public void putEvent(LinmotPutEvent putEvent) {
    // TODO Auto-generated method stub
    isActive = putEvent.control_word == LinmotPutConfiguration.CMD_OPERATION.getShort();
  }
}
