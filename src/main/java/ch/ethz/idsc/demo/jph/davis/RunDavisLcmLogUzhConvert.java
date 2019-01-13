// code by jph
package ch.ethz.idsc.demo.jph.davis;

import java.io.File;

import ch.ethz.idsc.gokart.lcm.davis.DavisLcmLogUzhConvert;

enum RunDavisLcmLogUzhConvert {
  ;
  public static void main(String[] args) {
    File file = new File("/home/ale/datasets/zuriscapes/rec_TRAINlong_lcm/lcmlog-2017-11-08.25");
    File target = new File("/home/ale/datasets/zuriscapes/rec_TRAINlong/");
    DavisLcmLogUzhConvert.of(file, target);
  }
}
