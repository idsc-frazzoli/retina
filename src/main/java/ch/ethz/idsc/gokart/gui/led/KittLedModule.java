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

  @Override
  protected void runAlgo() {
    LEDLcm.publish(GokartLcmChannel.LED_STATUS, new LEDStatus(++index % LEDStatus.NUM_LEDS));
  }

  @Override
  protected void first() {
    // ---
  }

  @Override
  protected void last() {
    // ---
  }

  @Override
  protected Scalar getPeriod() {
    return Quantity.of(0.2, SI.SECOND);
  }
}
