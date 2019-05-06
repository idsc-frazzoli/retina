// code by jph
package ch.ethz.idsc.gokart.dev.u3;

import ch.ethz.idsc.retina.u3.LabjackAdcFrame;
import ch.ethz.idsc.retina.u3.LabjackAdcListener;
import ch.ethz.idsc.retina.u3.LabjackU3LiveProviders;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.retina.util.sys.AbstractModule;

/** reads out labjack u3 device and publishes status of ADC to lcm */
/* package */ class LabjackU3LcmModule extends AbstractModule implements LabjackAdcListener {
  private final StartAndStoppable labjackU3LiveProvider = LabjackU3LiveProviders.create(this);

  @Override // from AbstractModule
  protected void first() {
    labjackU3LiveProvider.start();
  }

  @Override // from AbstractModule
  protected void last() {
    labjackU3LiveProvider.stop();
  }

  @Override // from LabjackAdcListener
  public void labjackAdc(LabjackAdcFrame labjackAdcFrame) {
    LabjackU3Publisher.accept(labjackAdcFrame);
  }
}
