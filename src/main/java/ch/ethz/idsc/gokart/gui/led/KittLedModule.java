// code by em
package ch.ethz.idsc.gokart.gui.led;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.led.LEDLcm;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractClockedModule;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public class KittLedModule extends AbstractClockedModule {
  private final int[] arrayIndex = new int[VirtualLedModule.NUM_LEDS];
  private int counterProgress;

  @Override
  protected void runAlgo() {
    counterProgress++;
    for (int i = 0; i < arrayIndex.length; i++)
      arrayIndex[i] = counterProgress % arrayIndex.length == i ? 1 : 0;
    LEDLcm.publish(GokartLcmChannel.LED_STATUS, arrayIndex);
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
    return Quantity.of(1, SI.SECOND);
  }
}
