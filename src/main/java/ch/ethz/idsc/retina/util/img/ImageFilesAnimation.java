// code by jph
package ch.ethz.idsc.retina.util.img;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import ch.ethz.idsc.tensor.io.AnimationWriter;

public enum ImageFilesAnimation {
  ;
  public static void gif(File output, int period_ms, Stream<File> stream) throws IOException, Exception {
    List<File> list = stream.collect(Collectors.toList());
    try (AnimationWriter animationWriter = AnimationWriter.of(output, period_ms)) {
      int count = 0;
      for (File file : list) {
        animationWriter.append(ImageIO.read(file));
        ++count;
        if (count % 100 == 0)
          System.out.println(count + " out of " + list.size());
      }
    }
  }
}
