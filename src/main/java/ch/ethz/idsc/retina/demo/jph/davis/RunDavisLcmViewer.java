// code by jph
package ch.ethz.idsc.retina.demo.jph.davis;

import ch.ethz.idsc.retina.demo.DavisSerial;
import ch.ethz.idsc.retina.lcm.davis.DavisLcmViewer;

enum RunDavisLcmViewer {
  ;
  public static void main(String[] args) {
    DavisLcmViewer.createStandlone(DavisSerial.FX2_02460045.name(), 30_000);
  }
}
