// code by jph
package ch.ethz.idsc.gokart.gui.top;

import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.core.pos.GokartPoseLocal;

@Deprecated
class LocalViewLcmModule extends ViewLcmModule {
  public LocalViewLcmModule() {
    setGokartPoseInterface(GokartPoseLocal.INSTANCE);
  }

  public static void standalone() throws Exception {
    LocalViewLcmModule localViewLcmModule = new LocalViewLcmModule();
    localViewLcmModule.first();
    localViewLcmModule.viewLcmFrame.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  public static void main(String[] args) throws Exception {
    standalone();
  }
}
