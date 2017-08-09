// code by jph
package ch.ethz.idsc.retina.demo.jph.hdl32e;

import ch.ethz.idsc.retina.dev.hdl32e.Hdl32ePositioningEvent;
import ch.ethz.idsc.retina.dev.hdl32e.Hdl32ePositioningListener;

public class PrintHdl32ePositioningListener implements Hdl32ePositioningListener {
  @Override
  public void positioning(Hdl32ePositioningEvent hdl32ePositioningEvent) {
    hdl32ePositioningEvent.print();
  }
}
