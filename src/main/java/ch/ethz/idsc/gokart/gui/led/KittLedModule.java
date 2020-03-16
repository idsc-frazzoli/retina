// code by em, gjoel
package ch.ethz.idsc.gokart.gui.led;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.led.LEDPutEvent;
import ch.ethz.idsc.gokart.dev.led.LEDPutProvider;
import ch.ethz.idsc.gokart.dev.led.LEDSocket;
import ch.ethz.idsc.gokart.dev.led.LEDStatus;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractClockedModule;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public class KittLedModule extends AbstractClockedModule implements LEDPutProvider {
  private int index = 0;
  private boolean increasing = true;
  private LEDStatus ledStatus = null;

  @Override // from AbstractClockedModule
  protected void runAlgo() {
    ledStatus = new LEDStatus((increasing ? ++index: --index) % LEDStatus.NUM_LEDS);
    increasing ^= index == 0 || index == LEDStatus.NUM_LEDS - 1;
  }

  @Override // from AbstractModule
  protected void first() {
    LEDSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from AbstractModule
  protected void last() {
    LEDSocket.INSTANCE.removePutProvider(this);
  }

  @Override // from AbstractClockedModule
  protected Scalar getPeriod() {
    return Quantity.of(0.1, SI.SECOND);
  }

  @Override // from LEDPutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.TESTING;
  }

  @Override // from LEDPutProvider
  public Optional<LEDPutEvent> putEvent() {
    return Optional.ofNullable(ledStatus).map(LEDPutEvent::from);
  }
}
