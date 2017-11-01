// code by jph
package ch.ethz.idsc.retina.demo.jph.davis;

import ch.ethz.idsc.retina.lcm.davis.DavisLcmViewer;

enum RunDavisLcmViewer {
  ;
  public static void main(String[] args) {
    DavisLcmViewer.createStandlone("overview", 30_000);
    //DavisLcmViewer.createQuickStandlone("overview", 30_000);
  }
}
