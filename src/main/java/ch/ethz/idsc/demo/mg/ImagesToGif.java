// code by jph
package ch.ethz.idsc.demo.mg;

import java.io.File;
import java.util.stream.Stream;

import ch.ethz.idsc.retina.util.img.ImageFilesAnimation;
import ch.ethz.idsc.tensor.io.HomeDirectory;

enum ImagesToGif {
  ;
  public static void main(String[] args) throws Exception {
    File dir = HomeDirectory.Pictures("gif", "slamTest");
    dir.mkdir();
    File output = HomeDirectory.Pictures("siliconEyePureEvents.gif");
    int period_ms = 50;
    Stream<File> stream = Stream.of(dir.listFiles()).sorted().limit(2500);
    ImageFilesAnimation.gif(output, period_ms, stream);
    System.out.println("Terminated");
  }
}
