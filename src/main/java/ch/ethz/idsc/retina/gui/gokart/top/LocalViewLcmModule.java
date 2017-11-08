// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.WindowConstants;

import ch.ethz.idsc.owly.car.core.VehicleModel;
import ch.ethz.idsc.owly.car.shop.RimoSinusIonModel;
import ch.ethz.idsc.owly.gui.TimerFrame;
import ch.ethz.idsc.owly.gui.ren.GridRender;
import ch.ethz.idsc.retina.lcm.autobox.GokartStatusLcmClient;
import ch.ethz.idsc.retina.lcm.autobox.LinmotGetLcmClient;
import ch.ethz.idsc.retina.lcm.autobox.RimoGetLcmClient;
import ch.ethz.idsc.retina.lcm.lidar.Mark8LcmHandler;
import ch.ethz.idsc.retina.lcm.lidar.Urg04lxLcmHandler;
import ch.ethz.idsc.retina.lcm.lidar.Vlp16LcmHandler;
import ch.ethz.idsc.retina.sys.AbstractModule;

public class LocalViewLcmModule extends AbstractModule {
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
      LidarRender lidarRender = new PlanarLidarRender(() -> SensorsConfig.GLOBAL.urg04lx);
      lidarRender.setColor(new Color(128, 192, 128, 64));
      urg04lxLcmHandler.lidarAngularFiringCollector.addListener(lidarRender);
      timerFrame.geometricComponent.addRenderInterface(lidarRender);
    }
    {
      LidarRender lidarRender = new ProjectedLidarRender(() -> SensorsConfig.GLOBAL.urg04lx);
      lidarRender.setColor(new Color(128, 0, 0, 128));
      urg04lxLcmHandler.lidarAngularFiringCollector.addListener(lidarRender);
      timerFrame.geometricComponent.addRenderInterface(lidarRender);
    }
    // ---
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
      LidarRender lidarRender = new ProjectedLidarRender(() -> SensorsConfig.GLOBAL.mark8);
      lidarRender.setColor(new Color(0, 128, 0, 128));
      mark8LcmHandler.lidarAngularFiringCollector.addListener(lidarRender);
      timerFrame.geometricComponent.addRenderInterface(lidarRender);
    }
    {
      LidarRender lidarRender = new ProjectedLidarRender(() -> SensorsConfig.GLOBAL.vlp16);
      lidarRender.setColor(new Color(0, 0, 128, 128));
      vlp16LcmHandler.lidarAngularFiringCollector.addListener(lidarRender);
      timerFrame.geometricComponent.addRenderInterface(lidarRender);
    }
    // ---
    rimoGetLcmClient.startSubscriptions();
    linmotGetLcmClient.startSubscriptions();
    gokartStatusLcmClient.startSubscriptions();
    // ---
    timerFrame.configCoordinateOffset(400, 500);
    timerFrame.jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
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

  public static void standalone() throws Exception {
    LocalViewLcmModule autoboxTestingModule = new LocalViewLcmModule();
    autoboxTestingModule.first();
    autoboxTestingModule.timerFrame.jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        // TODO Auto-generated method stub
      }

      @Override
      public void windowClosed(WindowEvent e) {
        Rectangle r = autoboxTestingModule.timerFrame.jFrame.getBounds();
        System.out.println(r);
      }
    });
    autoboxTestingModule.timerFrame.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  public static void main(String[] args) throws Exception {
    standalone();
  }
}
