// code by em, gjoel
package ch.ethz.idsc.gokart.dev.led;

import java.util.Optional;

import ch.ethz.idsc.owl.ani.api.ProviderRank;

// TODO replace by current steering angle indicator
/* package */ enum LEDPutFallback implements LEDPutProvider {
  INSTANCE;

  private int counter = 0;
  private int index = 0;
  private boolean increasing = true;
  private LEDStatus ledStatus = null;

  @Override // from LEDPutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.FALLBACK;
  }

  @Override // from LEDPutProvider
  public Optional<LEDPutEvent> putEvent() {
    if (counter++ % 10 == 0) {
      ledStatus = new LEDStatus((increasing ? ++index : --index) % LEDStatus.NUM_LEDS);
      increasing ^= index == 0 || index == LEDStatus.NUM_LEDS - 1;
    }
    return Optional.ofNullable(ledStatus).map(LEDPutEvent::from);
  }
}
