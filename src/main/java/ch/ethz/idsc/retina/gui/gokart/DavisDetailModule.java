// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.dev.davis.app.DavisDetailViewer;
import ch.ethz.idsc.retina.sys.AbstractModule;

public class DavisDetailModule extends AbstractModule {
  private DavisDetailViewer davisDetailViewer;

  @Override
  protected void first() throws Exception {
    davisDetailViewer = new DavisDetailViewer("overview", 10_000);
    davisDetailViewer.start();
  }

  @Override
  protected void last() {
    davisDetailViewer.stop();
  }

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
