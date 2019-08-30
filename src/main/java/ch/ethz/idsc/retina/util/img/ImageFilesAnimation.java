// code by jph
package ch.ethz.idsc.retina.util.img;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import ch.ethz.idsc.tensor.io.AnimationWriter;
import ch.ethz.idsc.tensor.io.GifAnimationWriter;

public enum ImageFilesAnimation {
  ;
  public static void gif(File output, int period_ms, Stream<File> stream) throws IOException, Exception {
    List<File> list = stream.collect(Collectors.toList());
    try (AnimationWriter animationWriter = new GifAnimationWriter(output, period_ms, TimeUnit.MILLISECONDS)) {
      int count = 0;
      for (File file : list) {
        animationWriter.write(ImageIO.read(file));
        ++count;
        if (count % 100 == 0)
          System.out.println(count + " out of " + list.size());
      }
    }
  }
}
