// code by em, gjoel
package ch.ethz.idsc.gokart.dev.led;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.steer.SteerColumnTracker;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

/* package */ enum LEDPutFallback implements LEDPutProvider {
  INSTANCE;

  private final SteerColumnTracker steerColumnTracker = SteerSocket.INSTANCE.getSteerColumnTracker();

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
    try {
      Clip clip = Clips.absolute(steerColumnTracker.getIntervalWidth() * 0.5);
      ledStatus = new LEDStatus(LEDIndexHelper.getIn(steerColumnTracker.getSteerColumnEncoderCentered(), clip));
    } catch (Exception e) {
      if (counter++ % 10 == 0) {
        ledStatus = new LEDStatus((increasing ? ++index : --index) % LEDStatus.NUM_LEDS);
        increasing ^= index == 0 || index == LEDStatus.NUM_LEDS - 1;
      }
    }
    return Optional.ofNullable(ledStatus).map(LEDPutEvent::from);
  }
}
