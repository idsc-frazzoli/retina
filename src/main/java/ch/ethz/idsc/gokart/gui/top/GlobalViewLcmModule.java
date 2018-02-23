// code by jph
package ch.ethz.idsc.gokart.gui.top;

import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmServer;
import ch.ethz.idsc.gokart.core.pos.GokartPoseOdometry;

public class GlobalViewLcmModule extends ViewLcmModule {
  private final GokartPoseOdometry gokartPoseOdometry = //
      GokartPoseLcmServer.INSTANCE.getGokartPoseOdometry();

  public GlobalViewLcmModule() {
    setGokartPoseInterface(gokartPoseOdometry);
  }

  public static void standalone() throws Exception {
    GlobalViewLcmModule globalViewLcmModule = new GlobalViewLcmModule();
    globalViewLcmModule.first();
    globalViewLcmModule.viewLcmFrame.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  public static void main(String[] args) throws Exception {
    standalone();
  }
}
