// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import java.awt.Color;

import ch.ethz.idsc.owly.car.core.VehicleModel;
import ch.ethz.idsc.owly.car.shop.RimoSinusIonModel;
import ch.ethz.idsc.owly.gui.TimerFrame;
import ch.ethz.idsc.owly.gui.ren.GridRender;
import ch.ethz.idsc.owly.math.se2.Se2Utils;
import ch.ethz.idsc.retina.lcm.autobox.GokartStatusLcmClient;
import ch.ethz.idsc.retina.lcm.autobox.LinmotGetLcmClient;
import ch.ethz.idsc.retina.lcm.autobox.RimoGetLcmClient;
import ch.ethz.idsc.retina.lcm.lidar.Mark8LcmHandler;
import ch.ethz.idsc.retina.lcm.lidar.Urg04lxLcmHandler;
import ch.ethz.idsc.retina.lcm.lidar.Vlp16LcmHandler;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class LocalViewLcmModule extends AbstractModule {
  private static final Tensor OFFSET_MARK8 = Se2Utils.toSE2Matrix(Tensors.vector(-0.35, 0.0, 0.1));
  private static final Tensor OFFSET_VLP16 = Se2Utils.toSE2Matrix(Tensors.vector(-0.43, 0.0, 0.025 + Math.PI / 2));
  /** angle calibrated on 2.10.2017 */
  private static final Tensor OFFSET_URG04 = Se2Utils.toSE2Matrix(Tensors.vector(1.2, 0.0, 0.05));
  // ---
  private final TimerFrame timerFrame = new TimerFrame();
  private final Urg04lxLcmHandler urg04lxLcmHandler = new Urg04lxLcmHandler("front");
  private final Mark8LcmHandler mark8LcmHandler = new Mark8LcmHandler("center");
  private final Vlp16LcmHandler vlp16LcmHandler = new Vlp16LcmHandler("center");
  private final RimoGetLcmClient rimoGetLcmClient = new RimoGetLcmClient();
  private final LinmotGetLcmClient linmotGetLcmClient = new LinmotGetLcmClient();
  private final GokartStatusLcmClient gokartStatusLcmClient = new GokartStatusLcmClient();

  @Override
  protected void first() throws Exception {
    timerFrame.geometricComponent.addRenderInterface(GridRender.INSTANCE);
    {
      TrigonometryRender trigonometryRender = new TrigonometryRender();
      gokartStatusLcmClient.addListener(trigonometryRender.gokartStatusListener);
      timerFrame.geometricComponent.addRenderInterface(trigonometryRender);
    }
    {
      PathRender pathRender = new PathRender();
      gokartStatusLcmClient.addListener(pathRender.gokartStatusListener);
      timerFrame.geometricComponent.addRenderInterface(pathRender);
    }
    // ---
    {
      LidarRender lidarRender = new LidarRender(OFFSET_URG04);
      lidarRender.setColor(new Color(128, 0, 0, 128));
      urg04lxLcmHandler.lidarAngularFiringCollector.addListener(lidarRender);
      timerFrame.geometricComponent.addRenderInterface(lidarRender);
    }
    final VehicleModel vehicleModel = RimoSinusIonModel.standard();
    timerFrame.geometricComponent.addRenderInterface(new VehicleFootprintRender(vehicleModel));
    // ---
    {
      GokartRender gokartRender = new GokartRender(vehicleModel);
      rimoGetLcmClient.addListener(gokartRender.rimoGetListener);
      linmotGetLcmClient.addListener(gokartRender.linmotGetListener);
      gokartStatusLcmClient.addListener(gokartRender.gokartStatusListener);
      timerFrame.geometricComponent.addRenderInterface(gokartRender);
    }
    // ---
    {
      LidarRender lidarRender = new LidarRender(OFFSET_MARK8);
      lidarRender.setColor(new Color(0, 128, 0, 128));
      mark8LcmHandler.lidarAngularFiringCollector.addListener(lidarRender);
      timerFrame.geometricComponent.addRenderInterface(lidarRender);
    }
    {
      LidarRender lidarRender = new LidarRender(OFFSET_VLP16);
      lidarRender.setColor(new Color(0, 0, 128, 128));
      vlp16LcmHandler.lidarAngularFiringCollector.addListener(lidarRender);
      timerFrame.geometricComponent.addRenderInterface(lidarRender);
    }
    // ---
    rimoGetLcmClient.startSubscriptions();
    linmotGetLcmClient.startSubscriptions();
    gokartStatusLcmClient.startSubscriptions();
    // ---
    timerFrame.jFrame.setVisible(true);
  }

  @Override
  protected void last() {
    urg04lxLcmHandler.stopSubscriptions();
    rimoGetLcmClient.stopSubscriptions();
    linmotGetLcmClient.stopSubscriptions();
    gokartStatusLcmClient.stopSubscriptions();
    timerFrame.close();
  }

  public static void main(String[] args) throws Exception {
    new LocalViewLcmModule().first();
  }
}
