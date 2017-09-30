// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import ch.ethz.idsc.owly.gui.TimerFrame;
import ch.ethz.idsc.owly.gui.ren.GridRender;
import ch.ethz.idsc.owly.model.car.VehicleModel;
import ch.ethz.idsc.owly.model.car.shop.RimoSinusIonModel;
import ch.ethz.idsc.retina.lcm.autobox.RimoGetLcmClient;
import ch.ethz.idsc.retina.lcm.lidar.SimpleUrg04lxLcmClient;
import ch.ethz.idsc.retina.sys.AbstractModule;

public class LocalViewLcmModule extends AbstractModule {
  private final TimerFrame timerFrame = new TimerFrame();
  private final SimpleUrg04lxLcmClient simpleUrg04lxLcmClient = new SimpleUrg04lxLcmClient("front");
  private final RimoGetLcmClient rimoGetLcmClient = new RimoGetLcmClient();

  @Override
  protected void first() throws Exception {
    timerFrame.geometricComponent.addRenderInterface(GridRender.INSTANCE);
    {
      Urg04lxRender urg04lxRender = new Urg04lxRender();
      simpleUrg04lxLcmClient.lidarAngularFiringCollector.addListener(urg04lxRender);
      timerFrame.geometricComponent.addRenderInterface(urg04lxRender);
    }
    final VehicleModel vehicleModel = RimoSinusIonModel.standard();
    timerFrame.geometricComponent.addRenderInterface(new VehicleFootprintRender(vehicleModel));
    GokartRender gokartRender = new GokartRender(vehicleModel);
    rimoGetLcmClient.addListener(gokartRender.rimoGetListener);
    timerFrame.geometricComponent.addRenderInterface(gokartRender);
    // ---
    rimoGetLcmClient.startSubscriptions();
    // ---
    timerFrame.jFrame.setVisible(true);
  }

  @Override
  protected void last() {
    simpleUrg04lxLcmClient.stopSubscriptions();
    rimoGetLcmClient.stopSubscriptions();
    timerFrame.close();
  }

  public static void main(String[] args) throws Exception {
    new LocalViewLcmModule().first();
  }
}
