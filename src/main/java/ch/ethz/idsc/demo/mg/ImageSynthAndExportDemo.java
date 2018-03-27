// code by jph
package ch.ethz.idsc.demo.mg;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.IOException;

import javax.imageio.ImageIO;

import ch.ethz.idsc.owl.bot.util.UserHome;

enum ImageSynthAndExportDemo {
  ;
  public static void main(String[] args) throws IOException {
    BufferedImage bufferedImage = new BufferedImage(240, 180, BufferedImage.TYPE_BYTE_GRAY);
    Graphics2D graphics = bufferedImage.createGraphics();
    {
      graphics.setColor(Color.WHITE);
      graphics.fillRect(0, 0, 240, 180);
      graphics.setColor(Color.BLACK);
      graphics.drawRect(10, 20, 40, 50);
    }
    {
      WritableRaster writableRaster = bufferedImage.getRaster();
      DataBufferByte dataBufferByte = (DataBufferByte) writableRaster.getDataBuffer();
      byte[] bytes = dataBufferByte.getData();
      for (int index = 0; index < 6000; ++index)
        bytes[index] = (byte) index;
    }
    {
      graphics.setColor(Color.GRAY);
      graphics.drawRect(20, 2, 40, 50);
    }
    int count = 0;
    ImageIO.write(bufferedImage, "png", UserHome.Pictures(String.format("example%03d.png", count)));
  }
}
