// code by jph
package ch.ethz.idsc.demo.jph.index;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.imageio.ImageIO;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.demo.jph.sys.DatahakiLogFileLocator;
import ch.ethz.idsc.gokart.offline.gui.GokartLcmImage;
import ch.ethz.idsc.gokart.offline.gui.GokartLogFileIndexer;
import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ enum LogIndexGaps {
  ;
  private static final int MIN_SIZE = 100_000_000;

  public static void main(String[] args) {
    File export = HomeDirectory.Pictures("logimages");
    export.mkdir();
    Set<String> set = Stream.of(GokartLogFile.values()) //
        .map(GokartLogFile::name) //
        .map(s -> s.substring(1)) //
        .collect(Collectors.toSet());
    List<File> folders = Stream.of(DatahakiLogFileLocator.ARCHIVE.listFiles()).sorted().collect(Collectors.toList());
    int count = 0;
    for (File folder : folders) {
      int compareTo = "20190919".compareTo(folder.getName());
      if (compareTo == 0) {
        List<File> files = Stream.of(folder.listFiles()).sorted().collect(Collectors.toList());
        for (File file : files) {
          String name = file.getName();
          if (name.endsWith(".lcm.00") && //
              MIN_SIZE < file.length()) {
            String tag = file.getName().substring(0, 24);
            if (!set.contains(tag)) {
              System.out.println(tag);
              try {
                GokartLogFileIndexer gokartLogFileIndexer = GokartLogFileIndexer.create(file);
                BufferedImage bufferedImage = GokartLcmImage.of(gokartLogFileIndexer);
                ImageIO.write(bufferedImage, "png", new File(export, tag + ".png"));
              } catch (Exception exception) {
                // ---
              }
              ++count;
            }
          }
        }
      }
    }
    System.out.println(count);
  }
}
