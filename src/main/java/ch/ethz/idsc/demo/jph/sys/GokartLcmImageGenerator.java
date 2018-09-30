// code by jph
package ch.ethz.idsc.demo.jph.sys;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.gokart.offline.gui.GokartLcmImage;
import ch.ethz.idsc.gokart.offline.gui.GokartLogFileIndexer;

enum GokartLcmImageGenerator {
  ;
  static final File DIRECTORY = new File("/media/datahaki/media/ethz/gokartlcmimage");

  public static void main(String[] args) {
    for (GokartLogFile gokartLogFile : GokartLogFile.range( //
        GokartLogFile._20180813T115544_26cfbbca, //
        GokartLogFile._20180927T162555_44599876)) {
      String title = gokartLogFile.getTitle() + ".png";
      File imageFile = new File(DIRECTORY, title);
      if (!imageFile.exists())
        try {
          System.out.println(gokartLogFile);
          File file = DatahakiLogFileLocator.INSTANCE.getAbsoluteFile(gokartLogFile);
          GokartLogFileIndexer gokartLogFileIndexer = GokartLogFileIndexer.create(file);
          BufferedImage bufferedImage = GokartLcmImage.of(gokartLogFileIndexer);
          ImageIO.write(bufferedImage, "png", imageFile);
        } catch (Exception exception) {
          exception.printStackTrace();
        }
    }
  }
}
