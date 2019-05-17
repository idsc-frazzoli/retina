// code by jph
package ch.ethz.idsc.gokart.dev.u3;

import java.util.Optional;

import ch.ethz.idsc.gokart.core.man.ManualConfig;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.joystick.ManualControlProvider;

public enum StaticManualControlProvider implements ManualControlProvider {
  INSTANCE;
  // ---
  private final GokartLabjackLcmClient gokartLabjackLcmClient = //
      new GokartLabjackLcmClient(GokartLcmChannel.LABJACK_U3_ADC, ManualConfig.GLOBAL.timeout);

  private StaticManualControlProvider() {
    gokartLabjackLcmClient.start();
  }

  @Override // from ManualControlProvider
  public Optional<ManualControlInterface> getManualControl() {
    return gokartLabjackLcmClient.getManualControl();
  }
}
