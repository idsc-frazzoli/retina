// code by em
package ch.ethz.idsc.gokart.lcm.led;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractClockedModule;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public class KittLedModule extends AbstractClockedModule {
  private int counterProgress;
  private int indexColor;
  private int[] array = new int[4];

  @Override
  protected void runAlgo() {
    counterProgress = counterProgress + 1;
    indexColor = counterProgress % 4;
    for (int i = 0; i < array.length; i++) {
      if (indexColor == i) {
        array[i] = 1;
      } else {
        array[i] = 0;
      }
    }
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
