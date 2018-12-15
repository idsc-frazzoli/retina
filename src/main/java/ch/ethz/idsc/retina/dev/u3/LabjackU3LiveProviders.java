// code by jph
package ch.ethz.idsc.retina.dev.u3;

import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.retina.util.VoidStartAndStoppable;

public enum LabjackU3LiveProviders {
  ;
  /** @param labjackAdcListener
   * @return new instance of LabjackU3LiveProvider if labjack device is feasible or VoidStartAndStoppable */
  public static StartAndStoppable create(LabjackAdcListener labjackAdcListener) {
    return LabjackU3LiveProvider.isFeasible() //
        ? new LabjackU3LiveProvider(labjackAdcListener)
        : VoidStartAndStoppable.INSTANCE;
  }
}
