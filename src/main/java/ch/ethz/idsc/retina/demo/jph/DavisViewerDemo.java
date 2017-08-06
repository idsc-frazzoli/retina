// code by jph
package ch.ethz.idsc.retina.demo.jph;

import ch.ethz.idsc.retina.dev.davis._240c.Davis240c;
import ch.ethz.idsc.retina.dvs.app.AedatLogViewer;

enum DavisViewerDemo {
  ;
  public static void main(String[] args) throws Exception {
    AedatLogViewer.of(Datahaki.LOG_03.file, Davis240c.INSTANCE);
  }
}
