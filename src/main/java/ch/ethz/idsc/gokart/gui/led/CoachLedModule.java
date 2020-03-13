// code by em, gjoel
package ch.ethz.idsc.gokart.gui.led;

import java.util.Optional;

import ch.ethz.idsc.gokart.dev.led.LEDPutEvent;
import ch.ethz.idsc.gokart.dev.led.LEDPutProvider;
import ch.ethz.idsc.gokart.dev.led.LEDSocket;
import ch.ethz.idsc.gokart.dev.led.LEDStatus;
import ch.ethz.idsc.owl.ani.api.ProviderRank;
import ch.ethz.idsc.retina.util.sys.AbstractClockedModule;
import ch.ethz.idsc.tensor.Scalar;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clips;

// TODO implement proper functionality
public class CoachLedModule extends AbstractClockedModule implements LEDPutProvider {
  private static final Distribution UNIFORM = UniformDistribution.of(Clips.positive(LEDStatus.NUM_LEDS));
  private LEDStatus ledStatus = null;

  @Override // from AbstractClockedModule
  protected void runAlgo() {
    ledStatus = new LEDStatus(randomInt(), randomInt());
  }

  private static int randomInt() {
    return RandomVariate.of(UNIFORM).number().intValue();
  }

  @Override // from LEDPutProvider
  public Optional<LEDPutEvent> putEvent() {
    return Optional.ofNullable(ledStatus).map(LEDPutEvent::from);
  }

  @Override // from LEDPutProvider
  public ProviderRank getProviderRank() {
    return ProviderRank.TESTING;
  }

  @Override // from AbstractClockedModule
  protected Scalar getPeriod() {
    return Quantity.of(0.5, SI.SECOND);
  }

  @Override // from AbstractModule
  protected void first() {
    LEDSocket.INSTANCE.addPutProvider(this);
  }

  @Override // from AbstractModule
  protected void last() {
    LEDSocket.INSTANCE.removePutProvider(this);
  }
}
