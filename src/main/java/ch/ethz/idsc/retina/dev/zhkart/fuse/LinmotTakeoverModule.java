// code by rvmoss and jph
package ch.ethz.idsc.retina.dev.zhkart.fuse;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutHelper;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutListener;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutProvider;
import ch.ethz.idsc.retina.dev.linmot.LinmotSocket;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.retina.util.data.TimedFuse;

/** module detects when human presses the break while the software
 * is controlling the break
 * 
 * module has to be stopped and restarted once fuse is blown */
public class LinmotTakeoverModule extends AbstractModule implements LinmotGetListener, LinmotPutListener, LinmotPutProvider {
  /** in order for fuse to blow, the position discrepancy
   * has to be maintained for 0.04[s] */
  private static final double DURATION_S = 0.04;
  /** position discrepancy threshold
   * anything below threshold is expected during normal operation */
  private static final double THRESHOLD_POS_DELTA = 20000;
  /** determined by operation status of linmot */
  private boolean isLinmotActive = false;
  private final TimedFuse timedFuse = new TimedFuse(DURATION_S);

  @Override // from AbstractModule
  protected void first() throws Exception {
    LinmotSocket.INSTANCE.addAll(this);
  }

  @Override // from AbstractModule
  protected void last() {
    LinmotSocket.INSTANCE.removeAll(this);
  }

  @Override // from LinmotGetListener
  public void getEvent(LinmotGetEvent getEvent) {
    timedFuse.register(isLinmotActive && getEvent.getPositionDiscrepancyRaw() >= THRESHOLD_POS_DELTA);
  }

  @Override // from LinmotPutListener
  public void putEvent(LinmotPutEvent putEvent) {
    isLinmotActive = putEvent.control_word == LinmotPutHelper.CMD_OPERATION.getShort();
  }

  @Override // from LinmotPutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.EMERGENCY;
  }

  @Override // from LinmotPutProvider
  public Optional<LinmotPutEvent> putEvent() {
    if (timedFuse.isBlown())
      return Optional.of(LinmotPutHelper.OFF_MODE_EVENT);
    return Optional.empty();
  }
}
