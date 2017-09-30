// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import ch.ethz.idsc.owly.gui.TimerFrame;
import ch.ethz.idsc.retina.sys.AbstractModule;

public class LocalViewModule extends AbstractModule {
  private final TimerFrame timerFrame = new TimerFrame();

  @Override
  protected void first() throws Exception {
    timerFrame.jFrame.setVisible(true);
  }

  @Override
  protected void last() {
    timerFrame.close();
  }
}
