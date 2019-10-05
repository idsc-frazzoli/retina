// code by jph
package ch.ethz.idsc.demo.jph.index;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import ch.ethz.idsc.gokart.offline.gui.GokartLogFileIndexer;
import ch.ethz.idsc.gokart.offline.gui.LogImageLabel;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.sca.Ceiling;

/* package */ class LogBookSplits {
  private static final File SPLITS = HomeDirectory.Pictures("logbook", "splits");
  private static final int MOD_X = 4 * 300;

  public static void main(String[] args) {
    SPLITS.mkdirs();
    GokartLogFileIndexer gokartLogFileIndexer = GokartLogFileIndexer.empty();
    for (File file : LogBookImages.FOLDER.listFiles())
      try {
        String name = file.getName().substring(0, 24);
        BufferedImage bufferedImage = ImageIO.read(file);
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        Scalar scalar = RationalScalar.of(width, MOD_X);
        int rows = Ceiling.of(scalar).number().intValue();
        for (int count = 0; count < rows; ++count) {
          BufferedImage master = new BufferedImage(MOD_X, height, BufferedImage.TYPE_INT_ARGB);
          Graphics2D graphics = master.createGraphics();
          graphics.drawImage(bufferedImage, -count * MOD_X, 0, null);
          LogImageLabel.of(graphics, 0, gokartLogFileIndexer);
          ImageIO.write(master, "png", new File(SPLITS, String.format("%s_%03d.png", name, count)));
        }
      } catch (Exception exception) {
        System.err.println(file);
      }
  }
}
