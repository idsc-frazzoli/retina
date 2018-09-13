// code by jph
package ch.ethz.idsc.demo.mg;

import java.io.File;
import java.util.stream.Stream;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.util.img.ImageFilesAnimation;

enum ImagesToGif {
  ;
  public static void main(String[] args) throws Exception {
    File dir = UserHome.Pictures("gif/slamTest");
    dir.mkdir();
    File output = UserHome.Pictures("slamCurveExtrapolate.gif");
    int period_ms = 150;
    Stream<File> stream = Stream.of(dir.listFiles()).sorted().limit(2500);
    ImageFilesAnimation.gif(output, period_ms, stream);
    System.out.println("Terminated");
  }
}
