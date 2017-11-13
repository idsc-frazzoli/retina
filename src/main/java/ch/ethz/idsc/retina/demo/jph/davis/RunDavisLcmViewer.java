// code by jph
package ch.ethz.idsc.retina.demo.jph.davis;

import ch.ethz.idsc.retina.dev.davis.app.DavisDetailViewer;

enum RunDavisLcmViewer {
  ;
  public static void main(String[] args) {
    DavisDetailViewer davisDetailViewer = new DavisDetailViewer("overview", 30_000);
    davisDetailViewer.start();
  }
}
