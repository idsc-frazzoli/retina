// code by jph
package ch.ethz.idsc.demo.vc;

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
    File dir = UserHome.Pictures("clusters");
    dir.mkdir();
    List<File> list = Stream.of(dir.listFiles()).sorted().limit(2500).collect(Collectors.toList());
    try (AnimationWriter animationWriter = AnimationWriter.of(UserHome.Pictures("clusterDemo.gif"), 100)) {
      int count = 0;
      for (File file : list) {
        BufferedImage bufferedImage = ImageIO.read(file);
        animationWriter.append(bufferedImage);
        count++;
        if (count % 100 == 0) {
          System.out.println(count + " out of " + list.size());
        }
      }
      System.out.println("Terminated");
    }
  }
}
