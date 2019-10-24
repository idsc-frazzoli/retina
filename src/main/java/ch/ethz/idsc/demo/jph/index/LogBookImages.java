// code by jph
package ch.ethz.idsc.demo.jph.index;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.demo.jph.sys.DatahakiLogFileLocator;
import ch.ethz.idsc.gokart.offline.gui.GokartLcmImage;
import ch.ethz.idsc.gokart.offline.gui.GokartLogFileIndexer;

/* package */ enum LogBookImages {
  ;
  static final File FOLDER = new File(LogBook.ROOT, "images");

  public static void all(List<File> list) {
    FOLDER.mkdirs();
    list.forEach(LogBookImages::process);
  }

  private static void process(File file) {
    if (Objects.nonNull(file) && //
        file.isFile()) {
      String name = file.getName().substring(0, 24);
      String year_mnth = name.substring(0, 6);
      File folder = new File(FOLDER, year_mnth);
      folder.mkdir();
      File target = new File(folder, name + ".png");
      if (!target.isFile())
        try {
          BufferedImage bufferedImage = GokartLcmImage.of(GokartLogFileIndexer.create(file));
          ImageIO.write(bufferedImage, "png", target);
        } catch (Exception exception) {
          System.err.println(file);
          // exception.printStackTrace();
        }
    } else
      System.out.println("skip " + file);
  }

  public static void main(String[] args) {
    // List<GokartLogFile> gokartLogFiles = Arrays.asList( //
    // GokartLogFile._20180503T094457_ce8724ba, //
    // GokartLogFile._20180514T155248_767e5417, //
    // GokartLogFile._20180517T152605_c1876fc4, //
    // GokartLogFile._20190701T170957_12dcbfa8, //
    // GokartLogFile._20190701T174938_12dcbfa8, //
    // GokartLogFile._20190912T173521_0f95cdcc, //
    // GokartLogFile._20190914T113023_11a994fa, //
    // GokartLogFile._20190927T110429_e9728d8b //
    // );
    List<GokartLogFile> list = new LinkedList<>();
    for (GokartLogFile gokartLogFile : GokartLogFile.values())
      if (GokartLogFile._20180503T094457_ce8724ba.compareTo(gokartLogFile) > 0)
        list.add(gokartLogFile);
    LogBookImages.all(list.stream().map(DatahakiLogFileLocator::file).collect(Collectors.toList()));
  }
}
