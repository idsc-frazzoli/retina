// code by jph
package ch.ethz.idsc.retina.dev.u3;

import ch.ethz.idsc.retina.util.StartAndStoppable;

public enum LabjackU3LiveProviders {
  ;
  public static StartAndStoppable create(LabjackAdcListener labjackAdcListener) {
    return LabjackU3LiveProvider.isFeasible() //
        ? new LabjackU3LiveProvider(labjackAdcListener)
        : LabjackU3VoidProvider.INSTANCE;
  }
}
