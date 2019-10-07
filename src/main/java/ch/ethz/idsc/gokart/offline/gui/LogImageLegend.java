// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Map.Entry;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ArrayPlot;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ImageResize;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.sca.Clip;

public enum LogImageLegend {
  ;
  private static final int ROW = 20;
  private static final int COL = 300;
  private static final Font FONT_LARGE = new Font(Font.DIALOG, Font.PLAIN, ROW - 5);

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
}
