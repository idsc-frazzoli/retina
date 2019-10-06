// code by jph
package ch.ethz.idsc.demo.jph.index;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

  /** @param year_mnth for example "201909"
   * @throws IOException */
  public static void compile(String year_mnth) throws IOException {
    SPLITS.mkdirs();
    File splits = new File(SPLITS, year_mnth);
    splits.mkdir();
    GokartLogFileIndexer gokartLogFileIndexer = GokartLogFileIndexer.empty();
    StringBuilder stringBuilder = new StringBuilder();
    String date_prev = "";
    List<File> files = Stream.of(new File(LogBookImages.FOLDER, year_mnth).listFiles()) //
        .sorted() //
        .collect(Collectors.toList());
    for (File file : files)
      try {
        String date = file.getName().substring(0, 8);
        if (!date_prev.equals(date)) {
          stringBuilder.append("\\chapter{" + formatChapter(date) + "}\n");
          date_prev = date;
        }
        String name = file.getName().substring(0, 24);
        BufferedImage bufferedImage = ImageIO.read(file);
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        Scalar scalar = RationalScalar.of(width, MOD_X);
        int rows = Ceiling.of(scalar).number().intValue();
        stringBuilder.append("\\newpage\n");
        stringBuilder.append("\\subsection*{" + formatSection(name) + " \\tt{" + name.replace("_", "\\_") + "}}\n");
        for (int count = 0; count < rows; ++count) {
          BufferedImage master = new BufferedImage(MOD_X, height, BufferedImage.TYPE_INT_ARGB);
          Graphics2D graphics = master.createGraphics();
          graphics.drawImage(bufferedImage, -count * MOD_X, 0, null);
          // ---
          LogImageLabel.of(graphics, 0, gokartLogFileIndexer);
          LogImageLabel.of(graphics, MOD_X, "" + (5 * (count + 1)) + "[min]");
          // ---
          String title = String.format("%s_%03d.png", name, count);
          ImageIO.write(master, "png", new File(splits, title));
          stringBuilder.append("\\includegraphics[width=\\textwidth]{splits/" + year_mnth + "/" + title + "}\n");
        }
      } catch (Exception exception) {
        System.err.println(file);
      }
    try (FileWriter fileWriter = new FileWriter(HomeDirectory.Pictures("logbook", "logs.tex"))) {
      fileWriter.write(stringBuilder.toString());
    }
  }

  private static String formatChapter(String date) {
    return String.format("%s-%s-%s", date.substring(0, 4), date.substring(4, 6), date.substring(6, 8));
  }

  private static String formatSection(String name) {
    return String.format("%s %s:%s:%s", //
        formatChapter(name), //
        name.substring(9, 11), //
        name.substring(11, 13), //
        name.substring(13, 15)
    // , //
    // name.substring(16)
    );
  }

  public static void main(String[] args) throws IOException {
    compile("201909");
  }
}
