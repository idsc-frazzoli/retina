// code by jph
package ch.ethz.idsc.gokart.offline.video;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import ch.ethz.idsc.tensor.io.HomeDirectory;

/* package */ enum FadeTop {
  ;
  public static void of(BufferedImage bufferedImage) {
    int width = bufferedImage.getWidth();
    int height = AccumulatedImageRender.HEIGHT;
    Graphics2D graphics = bufferedImage.createGraphics();
    Paint paint = new GradientPaint( //
        new Point2D.Double(0, 160), Color.WHITE, //
        new Point2D.Double(0, height), new Color(255, 255, 255, 192));
    graphics.setPaint(paint);
    graphics.fillRect(0, 0, width, height);
  }

  public static void main(String[] args) throws IOException {
    BufferedImage bufferedImage = ImageIO.read(HomeDirectory.Pictures("testimage.png"));
    of(bufferedImage);
    ImageIO.write(bufferedImage, "png", HomeDirectory.Pictures("testoutput.png"));
  }
}
