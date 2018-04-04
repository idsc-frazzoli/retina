// code by jph
package ch.ethz.idsc.demo.mg;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.IOException;

import javax.imageio.ImageIO;

import ch.ethz.idsc.owl.bot.util.UserHome;

// demo code for image generation
enum ImageSynthAndExportDemo {
  ;
  static BufferedImage createImage(int pix) {
    BufferedImage bufferedImage = new BufferedImage(240, 180, BufferedImage.TYPE_BYTE_GRAY);
    Graphics2D graphics = bufferedImage.createGraphics();
    graphics.setColor(Color.WHITE);
    graphics.fillRect(0, 0, 240, 180);
    graphics.setColor(Color.BLACK);
    graphics.drawRect(pix, 20, 40, 50);
    for (int i = 0; i < 10; i++) {
      AffineTransform old = graphics.getTransform();
      graphics.rotate(Math.toRadians(45), 110, 90);
      graphics.draw(new Ellipse2D.Float(100, 50, 20, 80));
      graphics.setTransform(old);
    }
    WritableRaster writableRaster = bufferedImage.getRaster();
    DataBufferByte dataBufferByte = (DataBufferByte) writableRaster.getDataBuffer();
    byte[] bytes = dataBufferByte.getData();
    for (int index = 0; index < 6000; ++index)
      bytes[index] = (byte) index;
    return bufferedImage;
  }

  public static void main(String[] args) throws IOException {
    BufferedImage bufferedImage = createImage(10);
    int count = 0;
    ImageIO.write(bufferedImage, "png", UserHome.Pictures(String.format("example%03d.png", count)));
  }
}
