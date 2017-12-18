// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.io.Import;

public enum StoreMapUtil {
  ;
  private static final File FILE = UserHome.Pictures("duebendorf.png");
  private static final int SIZE = 640;

  public static BufferedImage loadOrNull() {
    if (FILE.isFile())
      try {
        Tensor tensor = Import.of(FILE);
        return ImageFormat.of(tensor);
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    return null;
  }

  public static Tensor range() {
    return Tensors.vector(SIZE, SIZE).divide(RealScalar.of(7.5));
  }

  /** creates map and stores image at default location
   * 
   * @param geometricLayer
   * @param list */
  public static BufferedImage createNew(GeometricLayer geometricLayer, List<Tensor> list) {
    FILE.delete();
    BufferedImage bufferedImage = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_BYTE_GRAY);
    updateImage(geometricLayer, list, bufferedImage);
    Tensor tensor = ImageFormat.from(bufferedImage);
    try {
      Export.of(FILE, tensor);
      System.out.println("stored image at:\n" + FILE);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return StoreMapUtil.loadOrNull();
  }

  /** @param geometricLayer
   * @param list
   * @param bufferedImage */
  public static void updateMap(GeometricLayer geometricLayer, List<Tensor> list, BufferedImage bufferedImage) {
    try {
      updateImage(geometricLayer, list, bufferedImage);
      File file = UserHome.Pictures("map_" + System.nanoTime() + ".png");
      ImageIO.write(bufferedImage, "png", file);
      System.out.println("map exported to:\n" + file);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  private static void updateImage(GeometricLayer geometricLayer, List<Tensor> list, BufferedImage bufferedImage) {
    Graphics2D graphics = (Graphics2D) bufferedImage.getGraphics();
    graphics.setColor(Color.WHITE);
    for (Tensor pnts : list)
      graphics.draw(geometricLayer.toPath2D(pnts));
  }
}
