// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Optional;

import javax.imageio.ImageIO;

import ch.ethz.idsc.owl.bot.r2.ImageRegions;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.TensorRuntimeException;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.TensorRank;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.io.ResourceData;

// TODO split class into generic and specific functionality
public enum StoreMapUtil {
  ;
  private static final String REPO = "/map/dubendorf/hangar/20180122.png";
  private static final File FILE = UserHome.Pictures("duebendorf.png");
  private static final int SIZE = 640;

  public static BufferedImage loadOrNull() {
    try {
      Tensor tensor = grayscale(ResourceData.of(REPO));
      return ImageFormat.of(tensor);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return null;
  }

  /** grayscale images that encode free space (as black pixels) and the location of obstacles
   * (as non-black pixels) may be stored as grayscale, or indexed color images. Images with
   * colors palette may be of smaller size than the equivalent grayscale image. Indexed color
   * images have RGBA channels. The function converts the given image to a grayscale image if
   * necessary.
   * 
   * @param image
   * @return matrix with entries from the range {0, 1, ..., 255}
   * @throws Exception if input does not represent an image */
  // TODO obsolete with owl004
  private static Tensor grayscale(Tensor image) {
    Optional<Integer> optional = TensorRank.ofArray(image);
    switch (optional.get()) {
    case 2:
      return image.copy();
    case 3:
      return image.get(Tensor.ALL, Tensor.ALL, 0); // take RED channel for region member test
    }
    throw TensorRuntimeException.of(image);
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

  public static ImageRegion getImageRegion() throws Exception {
    return ImageRegions.loadFromRepository(REPO, range(), false);
  }
}
