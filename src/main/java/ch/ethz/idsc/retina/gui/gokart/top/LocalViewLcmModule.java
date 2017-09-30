// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import ch.ethz.idsc.owly.gui.TimerFrame;
import ch.ethz.idsc.owly.gui.ren.GridRender;
import ch.ethz.idsc.owly.model.car.VehicleModel;
import ch.ethz.idsc.owly.model.car.shop.RimoSinusIonModel;
import ch.ethz.idsc.retina.lcm.autobox.RimoGetLcmClient;
import ch.ethz.idsc.retina.lcm.lidar.Mark8LcmHandler;
import ch.ethz.idsc.retina.lcm.lidar.Urg04lxLcmHandler;
import ch.ethz.idsc.retina.sys.AbstractModule;

public class LocalViewLcmModule extends AbstractModule {
  private final TimerFrame timerFrame = new TimerFrame();
  private final Urg04lxLcmHandler urg04lxLcmHandler = new Urg04lxLcmHandler("front");
  private final Mark8LcmHandler mark8LcmHandler = new Mark8LcmHandler("center");
  private final RimoGetLcmClient rimoGetLcmClient = new RimoGetLcmClient();

  @Override
  protected void first() throws Exception {
    timerFrame.geometricComponent.addRenderInterface(GridRender.INSTANCE);
    {
      Urg04lxRender urg04lxRender = new Urg04lxRender();
      urg04lxLcmHandler.lidarAngularFiringCollector.addListener(urg04lxRender);
      timerFrame.geometricComponent.addRenderInterface(urg04lxRender);
    }
    final VehicleModel vehicleModel = RimoSinusIonModel.standard();
    timerFrame.geometricComponent.addRenderInterface(new VehicleFootprintRender(vehicleModel));
    // ---
    GokartRender gokartRender = new GokartRender(vehicleModel);
    rimoGetLcmClient.addListener(gokartRender.rimoGetListener);
    timerFrame.geometricComponent.addRenderInterface(gokartRender);
    // ---
    Mark8Render mark8Render = new Mark8Render();
    mark8LcmHandler.lidarAngularFiringCollector.addListener(mark8Render);
    timerFrame.geometricComponent.addRenderInterface(mark8Render);
    // ---
    rimoGetLcmClient.startSubscriptions();
    // ---
    timerFrame.jFrame.setVisible(true);
  }

  @Override
  protected void last() {
    urg04lxLcmHandler.stopSubscriptions();
    rimoGetLcmClient.stopSubscriptions();
    timerFrame.close();
  }

  public static void main(String[] args) throws Exception {
    new LocalViewLcmModule().first();
  }
}
