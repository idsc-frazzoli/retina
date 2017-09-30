// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import ch.ethz.idsc.owly.gui.TimerFrame;
import ch.ethz.idsc.owly.gui.ren.GridRender;
import ch.ethz.idsc.retina.lcm.lidar.SimpleUrg04lxLcmClient;
import ch.ethz.idsc.retina.sys.AbstractModule;

public class LocalViewLcmModule extends AbstractModule {
  private final TimerFrame timerFrame = new TimerFrame();
  private final SimpleUrg04lxLcmClient simpleUrg04lxLcmClient = new SimpleUrg04lxLcmClient("front");

  @Override
  protected void first() throws Exception {
    Urg04lxRender urg04lxRender = new Urg04lxRender();
    simpleUrg04lxLcmClient.lidarAngularFiringCollector.addListener(urg04lxRender);
    // ---
    timerFrame.geometricComponent.addRenderInterface(urg04lxRender);
    timerFrame.geometricComponent.addRenderInterface(new GokartRender());
    timerFrame.geometricComponent.addRenderInterface(GridRender.INSTANCE);
    timerFrame.jFrame.setVisible(true);
  }

  @Override
  protected void last() {
    simpleUrg04lxLcmClient.stopSubscriptions();
    timerFrame.close();
  }

  public static void main(String[] args) throws Exception {
    new LocalViewLcmModule().first();
  }
}
