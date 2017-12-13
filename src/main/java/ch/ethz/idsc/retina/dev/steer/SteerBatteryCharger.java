// code by jph
package ch.ethz.idsc.retina.dev.steer;

import java.util.Optional;

import ch.ethz.idsc.retina.dev.misc.MiscGetEvent;
import ch.ethz.idsc.retina.dev.misc.MiscGetListener;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.tensor.Scalars;

public enum SteerBatteryCharger implements MiscGetListener, SteerPutProvider {
  INSTANCE;
  // ---
  private boolean isCharging = true;

  @Override // from MiscGetListener
  public void getEvent(MiscGetEvent miscGetEvent) {
    isCharging = Scalars.lessThan(SteerConfig.GLOBAL.voltageHi, miscGetEvent.getSteerBatteryVoltage());
  }

  /***************************************************/
  @Override // from SteerPutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.PROTECTION;
  }

  @Override // from SteerPutProvider
  public Optional<SteerPutEvent> putEvent() {
    return Optional.ofNullable(isCharging ? SteerPutEvent.PASSIVE : null);
  }
}
