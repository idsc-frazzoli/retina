// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.gui.GraphicsUtil;
import ch.ethz.idsc.tensor.Tensor;

public enum StoreMapUtil {
  ;
  public static void createNew(GeometricLayer geometricLayer, List<Tensor> list) {
    try {
      final int SIZE = 640;
      BufferedImage bufferedImage = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_BYTE_GRAY);
      Graphics2D imageGraphics = (Graphics2D) bufferedImage.getGraphics();
      imageGraphics.setColor(Color.BLACK);
      imageGraphics.fillRect(0, 0, SIZE, SIZE);
      GraphicsUtil.setQualityHigh(imageGraphics);
      for (Tensor pnts : list) {
        Path2D path2D = geometricLayer.toPath2D(pnts);
        int col;
        col = 128;
        imageGraphics.setColor(new Color(col, col, col, 255));
        imageGraphics.setStroke(new BasicStroke(3.5f));
        imageGraphics.draw(path2D);
        col = 255;
        imageGraphics.setColor(new Color(col, col, col, 255));
        imageGraphics.setStroke(new BasicStroke(1.5f));
        imageGraphics.draw(path2D);
      }
      GraphicsUtil.setQualityDefault(imageGraphics);
      File file = UserHome.Pictures("map_" + System.nanoTime() + ".png");
      ImageIO.write(bufferedImage, "png", file);
      System.out.println("map exported to:\n" + file);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
