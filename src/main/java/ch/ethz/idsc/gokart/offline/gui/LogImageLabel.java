// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ArrayPlot;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ImageResize;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.sca.Clip;

public enum LogImageLabel {
  ;
  private static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, GokartLcmImage.FX + 2);
  private static final Color BACK0 = new Color(0, 0, 0, 192);
  private static final Color BACK1 = new Color(255, 255, 255, 192);
  private static final int ROW = 20;
  private static final int COL = 300;
  private static final Font FONT_LARGE = new Font(Font.DIALOG, Font.PLAIN, ROW - 5);

  public static void of(Graphics graphics, int pix, GokartLogFileIndexer gokartLogFileIndexer) {
    graphics.setFont(FONT);
    final int fx = GokartLcmImage.FX;
    graphics.setColor(BACK0);
    int piy = -0;
    for (GokartLogImageRow gokartLogImageRow : gokartLogFileIndexer.gokartLogImageRows)
      graphics.drawString(gokartLogImageRow.getName(), pix + 1, piy += fx);
    graphics.setColor(BACK1);
    piy = -1;
    for (GokartLogImageRow gokartLogImageRow : gokartLogFileIndexer.gokartLogImageRows)
      graphics.drawString(gokartLogImageRow.getName(), pix, piy += fx);
  }

  public static void of(Graphics graphics, int pix, String string) {
    graphics.setFont(FONT);
    FontMetrics fontMetrics = graphics.getFontMetrics();
    int stringWidth = fontMetrics.stringWidth(string);
    final int fx = GokartLcmImage.FX;
    graphics.setColor(BACK0);
    graphics.drawString(string, pix + 1 - stringWidth, fx);
    graphics.setColor(BACK1);
    graphics.drawString(string, pix - stringWidth, fx - 1);
  }

  public static void of(Graphics2D graphics, GokartLogFileIndexer gokartLogFileIndexer) {
    int piy = 0;
    GraphicsUtil.setQualityHigh(graphics);
    for (GokartLogImageRow gokartLogImageRow : gokartLogFileIndexer.gokartLogImageRows) {
      graphics.setColor(Color.BLACK);
      graphics.setFont(FONT_LARGE);
      FontMetrics fontMetrics = graphics.getFontMetrics();
      int text_y = piy + ROW - 5;
      graphics.drawString(gokartLogImageRow.getName(), 0, text_y);
      ColorDataGradient colorDataGradient = gokartLogImageRow.getColorDataGradient();
      if (gokartLogImageRow instanceof ClipLogImageRow) {
        ClipLogImageRow clipLogImageRow = (ClipLogImageRow) gokartLogImageRow;
        Tensor tensor = ImageResize.nearest(ArrayPlot.of(Tensors.of(Subdivide.of(0, 1, 257)), colorDataGradient), ROW - 2, 1);
        BufferedImage bufferedImage = ImageFormat.of(tensor);
        graphics.drawImage(bufferedImage, COL, piy, null);
        Clip clip = clipLogImageRow.clip();
        String min = clip.min().toString();
        int stringWidth = fontMetrics.stringWidth(min);
        graphics.drawString(min, COL - stringWidth, text_y);
        graphics.drawString("" + clip.max(), COL + 258, text_y);
      }
      if (gokartLogImageRow instanceof MappedLogImageRow) {
        MappedLogImageRow mappedLogImageRow = (MappedLogImageRow) gokartLogImageRow;
        int pix = COL;
        for (Entry<Scalar, String> entry : mappedLogImageRow.legend().entrySet()) {
          Scalar key = entry.getKey();
          Tensor tensor = ImageResize.nearest(Tensors.of(Tensors.of(key)).map(colorDataGradient), ROW - 2);
          BufferedImage bufferedImage = ImageFormat.of(tensor);
          graphics.drawImage(bufferedImage, pix, piy, null);
          String value = entry.getValue();
          graphics.drawString(value, pix + ROW, text_y);
          pix += 120;
        }
      }
      piy += ROW;
    }
  }

  public static void main(String[] args) throws IOException {
    BufferedImage bufferedImage = new BufferedImage(700, 380, BufferedImage.TYPE_INT_ARGB);
    of(bufferedImage.createGraphics(), GokartLogFileIndexer.empty());
    ImageIO.write(bufferedImage, "png", HomeDirectory.Pictures("legend.png"));
  }
}
