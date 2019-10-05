// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

public enum LogImageLabel {
  ;
  private static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, GokartLcmImage.FX + 2);
  private static final Color BACK0 = new Color(0, 0, 0, 192);
  private static final Color BACK1 = new Color(255, 255, 255, 192);

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
}
