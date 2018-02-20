// code by jph
package ch.ethz.idsc.demo.jph.davis;

import ch.ethz.idsc.retina.dev.davis.app.DavisDetailViewer;
import ch.ethz.idsc.retina.gui.gokart.GokartLcmChannel;

enum RunDavisLcmViewer {
  ;
  public static void main(String[] args) {
    DavisDetailViewer davisDetailViewer = new DavisDetailViewer(GokartLcmChannel.DAVIS_OVERVIEW);
    davisDetailViewer.start();
  }
}
