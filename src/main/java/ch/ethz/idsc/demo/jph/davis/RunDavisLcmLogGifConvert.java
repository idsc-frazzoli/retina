// code by jph
package ch.ethz.idsc.demo.jph.davis;

import java.io.File;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.lcm.davis.DavisLcmLogGifConvert;

enum RunDavisLcmLogGifConvert {
  ;
  public static void main(String[] args) {
    // File file = UserHome.file("20170908T141722_45dfaee7.lcm.00");
    // File file = UserHome.file("20170908T142504_45dfaee7.lcm.00");
    // File file = UserHome.file("20170911T155619_43995b66.lcm.00");
    // File file = UserHome.file("20170911T173346_43995b66.lcm.00");
    // File file = UserHome.file("20170911T172819_43995b66.lcm.00");
    // File file = UserHome.file("20170911T180635_43995b66.lcm.00");
    // File file = UserHome.file("20170911T182016_43995b66.lcm.00");
    // File file = UserHome.file("20170918T154009_2e37a549.lcm.00"); // not interesting
    // File file = UserHome.file("20170918T154100_2e37a549.lcm.00"); // ped + 3 guys
    // File file = UserHome.file("20170918T154307_2e37a549.lcm.00"); // cool peds
    File file = UserHome.file("20170918T154139_2e37a549.lcm.00"); // tram
    File target = UserHome.Pictures("");
    DavisLcmLogGifConvert.of(file, target);
  }
}
