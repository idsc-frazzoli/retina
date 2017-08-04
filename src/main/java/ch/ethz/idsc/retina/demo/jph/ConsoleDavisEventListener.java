// code by jph
package ch.ethz.idsc.retina.demo.jph;

import ch.ethz.idsc.retina.dev.davis240c.ApsDavisEvent;
import ch.ethz.idsc.retina.dev.davis240c.DavisEventListener;
import ch.ethz.idsc.retina.dev.davis240c.DvsDavisEvent;
import ch.ethz.idsc.retina.dev.davis240c.ImuDavisEvent;

public class ConsoleDavisEventListener implements DavisEventListener {
  @Override
  public void aps(ApsDavisEvent apsDavisEvent) {
    // if (apsDavisEvent.x == 239)
    // System.out.println(apsDavisEvent);
  }

  @Override
  public void dvs(DvsDavisEvent dvsDavisEvent) {
    // System.out.println(dvsDavisEvent);
  }

  @Override
  public void imu(ImuDavisEvent imuDavisEvent) {
    System.out.println(imuDavisEvent);
  }
}
