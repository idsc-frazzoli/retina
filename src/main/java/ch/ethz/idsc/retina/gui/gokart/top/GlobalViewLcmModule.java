// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.dev.zhkart.pos.OdometryLcmClient;

public class GlobalViewLcmModule extends ViewLcmModule {
  private final OdometryLcmClient odometryLcmClient = new OdometryLcmClient();

  public GlobalViewLcmModule() {
    setGokartPoseInterface(odometryLcmClient.gokartPoseOdometry);
  }

  @Override // from AbstractModule
  protected void first() throws Exception {
    super.first();
    odometryLcmClient.startSubscriptions();
  }

  @Override // from AbstractModule
  protected void last() {
    odometryLcmClient.stopSubscriptions();
    super.last();
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
