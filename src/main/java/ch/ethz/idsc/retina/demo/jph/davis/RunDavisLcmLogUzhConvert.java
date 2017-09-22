// code by jph
package ch.ethz.idsc.retina.demo.jph.davis;

import java.io.File;

import ch.ethz.idsc.retina.lcm.davis.DavisLcmLogUzhConvert;

enum RunDavisLcmLogUzhConvert {
  ;
  public static void main(String[] args) {
    File file = new File("/media/datahaki/media/ethz/lcmlog", //
        // "20170919T175802_ce08b2c6.lcm.00_localize_hallway");
        "20170919T180316_ce08b2c6.lcm.00_localize_hallway");
    File target = new File("/media/datahaki/Transcend/_Datahaki_SHILI");
    DavisLcmLogUzhConvert.of(file, target);
  }
}
