// code by jph
package ch.ethz.idsc.retina.demo.jph.davis;

import java.io.File;

import ch.ethz.idsc.retina.lcm.davis.DavisLcmLogGifConvert;
import ch.ethz.idsc.retina.util.io.UserHome;

enum RunDavisLcmLogGifConvert {
  ;
  public static void main(String[] args) {
    // File file = UserHome.file("20170908T141722_45dfaee7.lcm.00");
    // File file = UserHome.file("20170908T142504_45dfaee7.lcm.00");
    // File file = UserHome.file("20170911T155619_43995b66.lcm.00");
    // File file = UserHome.file("20170911T173346_43995b66.lcm.00");
    // File file = UserHome.file("20170911T172819_43995b66.lcm.00");
    // File file = UserHome.file("20170911T180635_43995b66.lcm.00");
    File file = UserHome.file("20170911T182016_43995b66.lcm.00");
    File target = UserHome.Pictures("");
    DavisLcmLogGifConvert.of(file, target);
  }
}
