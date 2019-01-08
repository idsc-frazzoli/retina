// code by jph
package ch.ethz.idsc.gokart.core.slam;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ch.ethz.idsc.owl.bot.r2.ImageEdges;
import ch.ethz.idsc.owl.bot.r2.ImageRegions;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.io.ResourceData;

/** static resource of Dubendorf hangar
 * name refers to png-file in repository, do not rename!
 * 
 * the predefined geometry is crucial for lidar-based localization */
// TODO not necessary to mix localization and obstacles, for instance extrusion only required in one case
public enum PredefinedMap implements LocalizationImage {
  /** image of known static obstacles */
  // DUBILAB_OBSTACLES_20180423(7.5), //
  /** image, no tents, no aerotain */
  // DUBILAB_LOCALIZATION_20180506(7.5), //
  /** image of known static obstacles
   * image with central tents, bus tent, and aerotain */
  // DUBILAB_OBSTACLES_20180610(7.5), //
  // DUBILAB_LOCALIZATION_20180702(7.5), //
  /** empty space */
  DUBILAB_OBSTACLES_20180703(7.5), //
  /** empty space with tents */
  // DUBILAB_OBSTACLES_20180704(7.5), //
  /** only the first tent */
  // DUBILAB_LOCALIZATION_20180705(7.5), //
  /** only front tent, four balloons in the aerotain area */
  // DUBILAB_LOCALIZATION_20180813(7.5), //
  /** only front tent, aerotain poster shifted, no balloons */
  DUBILAB_LOCALIZATION_20180901(7.5), //
  /** no tents, aerotain poster shifted, no balloons */
  // DUBILAB_LOCALIZATION_20180904(7.5), //
  /** car and house tents in new positions for tse2 planning */
  // DUBILAB_LOCALIZATION_20180912(7.5), //
  /** tents at parking position */
  DUBILAB_LOCALIZATION_20181128(7.5), //
  ;
  /** number of pixels to extrude geometry for localization */
  private static final int TTL = 3;
  /** assume void, i.e. no obstacle, in area outside of image */
  private static final int RGBA_VOID = 0;
  // ---
  /** meter to pixel */
  private final Scalar scale;
  private final BufferedImage bufferedImage;
  /** size == width == height of square bufferedImage */
  private final int size;
  private final BufferedImage extrudedImage;
  private final ImageRegion imageRegion;
  private final Tensor model2pixel;

  /** @param meter_to_pixel for instance 1[m] may correspond to 7.5 pixel */
  private PredefinedMap(double meter_to_pixel) {
    this.scale = DoubleScalar.of(meter_to_pixel);
    String string = String.format("/%s.png", name().replace('_', File.separatorChar).toLowerCase());
    Tensor tensor = ImageRegions.grayscale(ResourceData.of(string));
    bufferedImage = ImageFormat.of(tensor);
    this.size = bufferedImage.getWidth();
    extrudedImage = ImageFormat.of(ImageEdges.extrusion(tensor, TTL));
    imageRegion = new ImageRegion(tensor, range(), false);
    // ---
    if (bufferedImage.getHeight() != size)
      new RuntimeException("map image not squared").printStackTrace();
    double s = scale.number().doubleValue();
    int h = bufferedImage.getHeight();
    model2pixel = Tensors.matrix(new Number[][] { //
        { s, 0, 0 }, //
        { 0, -s, h }, //
        { 0, 0, 1 }, //
    }).unmodifiable();
  }

  /** the map for visualization is strictly black and white
   * 
   * @return instance of map used for visualization */
  @Override
  public BufferedImage getImage() {
    return bufferedImage;
  }

  /** the map for localization has grayscale values in the proximity of static geometry
   * 
   * @return instance of extruded map used for lidar-based localization */
  @Override
  public BufferedImage getImageExtruded() {
    return extrudedImage;
  }

  /** @param point2d
   * @return color value at pixel in given location */
  public int getRGB(Point2D point2d) {
    // LONGTERM refactor: bufferedImage, size, color lookup can go in wrapper class?
    int pix = (int) point2d.getX();
    if (0 <= pix && pix < size) {
      int piy = (int) point2d.getY();
      if (0 <= piy && piy < size)
        return bufferedImage.getRGB(pix, piy);
    }
    return RGBA_VOID;
  }

  /** @return image region currently used only for visualization */
  public ImageRegion getImageRegion() {
    return imageRegion;
  }

  /** for the dubendorf hangar the range of the map image is
   * {85.333, 85.333} which corresponds to ~85.3 meter width and height
   * 
   * @return */
  public Tensor range() {
    return Tensors.vector(size, size).divide(scale);
  }

  /** @return 3x3 matrix */
  @Override
  public Tensor getModel2Pixel() {
    return model2pixel;
  }

  /** @return meter to pixel */
  public Scalar scale() {
    return scale;
  }

  public static void main(String[] args) throws IOException {
    ImageIO.write(DUBILAB_LOCALIZATION_20181128.bufferedImage, "png", HomeDirectory.file("20181128.png"));
  }
}
