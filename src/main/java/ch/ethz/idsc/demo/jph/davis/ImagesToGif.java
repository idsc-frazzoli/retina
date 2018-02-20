// code by jph
package ch.ethz.idsc.demo.jph.davis;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.tensor.io.AnimationWriter;

enum ImagesToGif {
  ;
  public static void main(String[] args) throws Exception {
    File dir = UserHome.Pictures("dvs");
    dir.mkdir();
    // ImageIO.write(bufferedImage, "png", new File(dir, String.format("dvs%05d.png", ++count)));
    List<File> list = Stream.of(dir.listFiles()).sorted().limit(10000).collect(Collectors.toList());
    try (AnimationWriter animationWriter = AnimationWriter.of(UserHome.Pictures("dvs_xxxx.gif"), 100)) {
      // int count = 0;
      for (File file : list) {
        BufferedImage bufferedImage = ImageIO.read(file);
        animationWriter.append(bufferedImage);
      }
    }
  }
}
