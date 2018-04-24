// code by jph
package ch.ethz.idsc.demo.jph.davis;

import java.io.File;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.lcm.davis.DavisLcmLogGifConvert;

enum RunDavisLcmLogGifConvert {
  ;
  public static void main(String[] args) {
    // File file = UserHome.file("20170918T154100_2e37a549.lcm.00"); // ped + 3 guys
    // File file = UserHome.file("20170918T154307_2e37a549.lcm.00"); // cool peds
    File file = UserHome.file("20170918T154139_2e37a549.lcm.00"); // tram
    File target = UserHome.Pictures("");
    DavisLcmLogGifConvert.of(file, target);
  }
}
