// code by em, gjoel
package ch.ethz.idsc.gokart.gui.led;

import ch.ethz.idsc.gokart.dev.led.LEDStatus;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.led.LEDLcm;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractClockedModule;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public class KittLedModule extends AbstractClockedModule {
  private int index = 0;
  private boolean increasing = true;

  @Override // from AbstractClockedModule
  protected void runAlgo() {
    LEDLcm.publish(GokartLcmChannel.LED_STATUS, new LEDStatus((increasing ? ++index: --index) % LEDStatus.NUM_LEDS));
    increasing ^= index == 0 || index == LEDStatus.NUM_LEDS - 1;
  }

  @Override // from AbstractModule
  protected void first() {
    // ---
  }

  @Override // from AbstractModule
  protected void last() {
    // ---
  }

  @Override // from AbstractClockedModule
  protected Scalar getPeriod() {
    return Quantity.of(0.1, SI.SECOND);
  }
}
