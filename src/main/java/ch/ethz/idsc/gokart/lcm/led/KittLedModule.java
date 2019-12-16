// code by em
package ch.ethz.idsc.gokart.lcm.led;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractClockedModule;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public class KittLedModule extends AbstractClockedModule {
  private int counterProgress;
  private int indexColor;
  private int[] arrayIndex = new int[4];

  @Override
  protected void runAlgo() {
    counterProgress = counterProgress + 1;
    indexColor = counterProgress % 4;
    for (int i = 0; i < arrayIndex.length; i++) {
      if (indexColor == i) {
        arrayIndex[i] = 1;
      } else {
        arrayIndex[i] = 0;
      }
    }
    LEDLcm.publish(GokartLcmChannel.LED_STATUS, arrayIndex);
  }

  @Override
  protected void first() {
  }

  @Override
  protected void last() {
  }

  @Override
  protected Scalar getPeriod() {
    return Quantity.of(1, SI.SECOND);
  }
}
