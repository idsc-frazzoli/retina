// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.dev.zhkart.pos.GokartPoseLocal;

public class LocalViewLcmModule extends ViewLcmModule {
  public LocalViewLcmModule() {
    setGokartPoseInterface(GokartPoseLocal.INSTANCE);
  }

  public static void standalone() throws Exception {
    LocalViewLcmModule localViewLcmModule = new LocalViewLcmModule();
    localViewLcmModule.first();
    localViewLcmModule.timerFrame.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  public static void main(String[] args) throws Exception {
    standalone();
  }
}
