// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import javax.swing.WindowConstants;

import ch.ethz.idsc.owl.gui.win.TimerFrame;
import ch.ethz.idsc.retina.dev.zhkart.pos.OdometryLcmClient;
import ch.ethz.idsc.retina.gui.gokart.GokartLcmChannel;
import ch.ethz.idsc.retina.lcm.lidar.Vlp16LcmHandler;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.retina.sys.AppCustomization;
import ch.ethz.idsc.retina.util.gui.WindowConfiguration;

public class GlobalViewLcmModule extends AbstractModule {
  private final TimerFrame timerFrame = new TimerFrame();
  private final OdometryLcmClient odometryLcmClient = new OdometryLcmClient();
  private final Vlp16LcmHandler vlp16LcmHandler = new Vlp16LcmHandler(GokartLcmChannel.VLP16_CENTER);
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());

  @Override // from AbstractModule
  protected void first() throws Exception {
    timerFrame.geometricComponent.addRenderInterface(GridRender.INSTANCE);
    {
      GlobalGokartRender globalGokartRender = new GlobalGokartRender(odometryLcmClient.gokartOdometry);
      // globalGokartRender.
      vlp16LcmHandler.lidarAngularFiringCollector.addListener(globalGokartRender);
      // LidarRender lidarRender = new PerspectiveLidarRender(() -> SensorsConfig.GLOBAL.vlp16);
      // lidarRender.setColor(new Color(128, 0, 0, 255));
      // vlp16LcmHandler.lidarAngularFiringCollector.addListener(lidarRender);
      timerFrame.geometricComponent.addRenderInterface(globalGokartRender);
    }
    // ---
    odometryLcmClient.startSubscriptions();
    // ---
    windowConfiguration.attach(getClass(), timerFrame.jFrame);
    timerFrame.configCoordinateOffset(400, 500);
    timerFrame.jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    timerFrame.jFrame.setVisible(true);
  }

  @Override // from AbstractModule
  protected void last() {
    odometryLcmClient.stopSubscriptions();
    timerFrame.close();
  }

  public static void standalone() throws Exception {
    GlobalViewLcmModule globalViewLcmModule = new GlobalViewLcmModule();
    globalViewLcmModule.first();
    globalViewLcmModule.timerFrame.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  public static void main(String[] args) throws Exception {
    standalone();
  }
}
