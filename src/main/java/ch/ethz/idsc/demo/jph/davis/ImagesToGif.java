// code by jph
package ch.ethz.idsc.demo.jph.davis;

import java.io.File;
import java.util.stream.Stream;

import ch.ethz.idsc.retina.util.img.ImageFilesAnimation;
import ch.ethz.idsc.tensor.io.HomeDirectory;

enum ImagesToGif {
  ;
  public static void main(String[] args) throws Exception {
    File dir = HomeDirectory.Pictures("dvs");
    dir.mkdir();
    // ImageIO.write(bufferedImage, "png", new File(dir, String.format("dvs%05d.png", ++count)));
    File output = HomeDirectory.Pictures("dvs_xxxx.gif");
    int period_ms = 100;
    Stream<File> stream = Stream.of(dir.listFiles()).sorted().limit(10000);
    ImageFilesAnimation.gif(output, period_ms, stream);
  }
}
