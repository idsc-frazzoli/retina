// code by jph
package ch.ethz.idsc.gokart.gui;

import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.lcm.davis.DavisDetailViewer;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;

public class DavisDetailModule extends AbstractModule {
  private DavisDetailViewer davisDetailViewer = //
      new DavisDetailViewer(GokartLcmChannel.DAVIS_OVERVIEW);
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());

  @Override // from AbstractModule
  protected void first() throws Exception {
    windowConfiguration.attach(getClass(), davisDetailViewer.davisViewerFrame.jFrame);
    davisDetailViewer.davisViewerFrame.jFrame.setVisible(true);
    davisDetailViewer.start();
  }

  @Override // from AbstractModule
  protected void last() {
    davisDetailViewer.stop();
  }

  /***************************************************/
  public static void standalone() throws Exception {
    DavisDetailModule davisDetailModule = new DavisDetailModule();
    davisDetailModule.first();
    davisDetailModule.davisDetailViewer.davisViewerFrame // that's a bit much :-(
        .jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  public static void main(String[] args) throws Exception {
    standalone();
  }
}
