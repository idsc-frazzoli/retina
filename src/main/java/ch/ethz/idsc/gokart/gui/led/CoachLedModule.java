// code by em, gjoel
package ch.ethz.idsc.gokart.gui.led;

import ch.ethz.idsc.gokart.dev.led.LEDStatus;
import ch.ethz.idsc.gokart.lcm.led.LEDLcm;
import ch.ethz.idsc.retina.util.sys.AbstractClockedModule;
import ch.ethz.idsc.tensor.Scalar;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clips;

public class CoachLedModule extends AbstractClockedModule {
  private static final Distribution UNIFORM = UniformDistribution.of(Clips.positive(LEDStatus.NUM_LEDS));

  @Override // from AbstractClockedModule
  protected void runAlgo() {
    LEDLcm.publish(GokartLcmChannel.LED_STATUS, new LEDStatus(randomInt(), randomInt()));
  }

  private static int randomInt() {
    return RandomVariate.of(UNIFORM).number().intValue();
  }

  @Override // from AbstractClockedModule
  protected Scalar getPeriod() {
    return Quantity.of(0.5, SI.SECOND);
  }

  @Override // from AbstractModule
  protected void first() {
    // ---
  }

  @Override // from AbstractModule
  protected void last() {
    // ---
  }
}
