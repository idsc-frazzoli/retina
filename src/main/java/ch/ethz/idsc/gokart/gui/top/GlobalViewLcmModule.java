// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmServer;
import ch.ethz.idsc.gokart.core.pos.GokartPoseOdometry;
import ch.ethz.idsc.gokart.core.slam.LidarLocalizationModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;

public class GlobalViewLcmModule extends ViewLcmModule {
  private final GokartPoseOdometry gokartPoseOdometry = GokartPoseLcmServer.INSTANCE.getGokartPoseOdometry();

  public GlobalViewLcmModule() {
    setGokartPoseInterface(gokartPoseOdometry);
  }

  public static void standalone() throws Exception {
    GlobalViewLcmModule globalViewLcmModule = new GlobalViewLcmModule();
    globalViewLcmModule.first();
    globalViewLcmModule.viewLcmFrame.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    // ---
    ModuleAuto.INSTANCE.runOne(LidarLocalizationModule.class);
    globalViewLcmModule.viewLcmFrame.jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        ModuleAuto.INSTANCE.endOne(LidarLocalizationModule.class);
      }
    });
  }

  public static void main(String[] args) throws Exception {
    standalone();
  }
}
