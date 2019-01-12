// code by jph
package ch.ethz.idsc.retina.u3;

import ch.ethz.idsc.retina.util.EmptyStartAndStoppable;
import ch.ethz.idsc.retina.util.StartAndStoppable;

public enum LabjackU3LiveProviders {
  ;
  /** @param labjackAdcListener
   * @return new instance of LabjackU3LiveProvider if labjack device is feasible or VoidStartAndStoppable */
  public static StartAndStoppable create(LabjackAdcListener labjackAdcListener) {
    return LabjackU3LiveProvider.isFeasible() //
        ? new LabjackU3LiveProvider(labjackAdcListener)
        : EmptyStartAndStoppable.INSTANCE;
  }
}
