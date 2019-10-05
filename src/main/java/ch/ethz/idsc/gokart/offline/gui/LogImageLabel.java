// code by jph
package ch.ethz.idsc.gokart.offline.gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public enum LogImageLabel {
  ;
  private static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, GokartLcmImage.FX + 2);

  public static void of(Graphics graphics, int pix, GokartLogFileIndexer gokartLogFileIndexer) {
    graphics.setFont(FONT);
    final int fx = GokartLcmImage.FX;
    graphics.setColor(new Color(0, 0, 0, 128));
    int piy = -0;
    for (GokartLogImageRow gokartLogImageRow : gokartLogFileIndexer.gokartLogImageRows)
      graphics.drawString(gokartLogImageRow.getName(), pix + 1, piy += fx);
    graphics.setColor(new Color(255, 255, 255, 128));
    piy = -1;
    for (GokartLogImageRow gokartLogImageRow : gokartLogFileIndexer.gokartLogImageRows)
      graphics.drawString(gokartLogImageRow.getName(), pix, piy += fx);
  }
}
