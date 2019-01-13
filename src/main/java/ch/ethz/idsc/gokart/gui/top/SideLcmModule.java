// code by vc, jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;

import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.core.pos.GokartPoseLocal;
import ch.ethz.idsc.gokart.core.pos.MappedPoseInterface;
import ch.ethz.idsc.gokart.lcm.lidar.Vlp16LcmHandler;
import ch.ethz.idsc.owl.gui.ren.GridRender;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;

public class SideLcmModule extends AbstractModule {
  protected final ViewLcmFrame viewLcmFrame = new ViewLcmFrame();
  private final Vlp16LcmHandler vlp16LcmHandler = SensorsConfig.GLOBAL.vlp16LcmHandler();
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());
  private MappedPoseInterface gokartPoseInterface = GokartPoseLocal.INSTANCE;

  protected void setGokartPoseInterface(MappedPoseInterface gokartPoseInterface) {
    this.gokartPoseInterface = gokartPoseInterface;
    viewLcmFrame.setGokartPoseInterface(gokartPoseInterface);
  }

  @Override // from AbstractModule
  protected void first() throws Exception {
    {
      LidarRender lidarRender = new SideLidarRender(gokartPoseInterface);
      lidarRender.setColor(new Color(0, 0, 128, 128));
      vlp16LcmHandler.lidarAngularFiringCollector.addListener(lidarRender);
      viewLcmFrame.geometricComponent.addRenderInterface(lidarRender);
    }
    {
      LidarRender lidarRender = new SideObstacleLidarRender(gokartPoseInterface);
      lidarRender.setColor(new Color(255, 0, 0, 128));
      lidarRender.pointSize = 4;
      vlp16LcmHandler.lidarAngularFiringCollector.addListener(lidarRender);
      viewLcmFrame.geometricComponent.addRenderInterface(lidarRender);
    }
    {
      viewLcmFrame.geometricComponent.addRenderInterface(new SideGokartRender());
    }
    // {
    // LidarRender lidarRender = new PerspectiveLidarRender(() -> SensorsConfig.GLOBAL.vlp16);
    // // lidarRender.setColor(new Color(128, 0, 0, 255));
    // vlp16LcmHandler.lidarAngularFiringCollector.addListener(lidarRender);
    // timerFrame.geometricComponent.addRenderInterface(lidarRender);
    // }
    viewLcmFrame.geometricComponent.addRenderInterface(GridRender.INSTANCE);
    // ---
    vlp16LcmHandler.startSubscriptions();
    // ---
    // odometryLcmClient.startSubscriptions();
    // ---
    windowConfiguration.attach(getClass(), viewLcmFrame.jFrame);
    viewLcmFrame.configCoordinateOffset(400, 500);
    viewLcmFrame.jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    viewLcmFrame.jFrame.setVisible(true);
  }

  @Override // from AbstractModule
  protected void last() {
    // ---
    // odometryLcmClient.stopSubscriptions();
    // ---
    vlp16LcmHandler.stopSubscriptions();
    viewLcmFrame.close();
  }

  public static void standalone() throws Exception {
    SideLcmModule sideLcmModule = new SideLcmModule();
    sideLcmModule.first();
    sideLcmModule.viewLcmFrame.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  public static void main(String[] args) throws Exception {
    standalone();
  }
}
