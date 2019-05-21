// code by jph
package ch.ethz.idsc.demo.jph.sys;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import ch.ethz.idsc.demo.GokartLogFile;
import ch.ethz.idsc.gokart.offline.gui.GokartLcmImage;
import ch.ethz.idsc.gokart.offline.gui.GokartLogFileIndexer;

/** for each log file in range export overview image */
/* package */ enum GokartLcmImageGenerator {
  ;
  static final File DIRECTORY = new File("/media/datahaki/media/ethz/gokartlcmimage");

  public static void main(String[] args) {
    for (GokartLogFile gokartLogFile : GokartLogFile.range( //
        GokartLogFile._20190502T105037_bdbf8063, //
        GokartLogFile._20190516T185634_bcf7fd52)) {
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
