// code by jph
package ch.ethz.idsc.demo.jph.davis;

import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.lcm.davis.DavisDetailViewer;

enum RunDavisLcmViewer {
  ;
  public static void main(String[] args) {
    DavisDetailViewer davisDetailViewer = new DavisDetailViewer(GokartLcmChannel.DAVIS_OVERVIEW);
    davisDetailViewer.start();
  }
}
