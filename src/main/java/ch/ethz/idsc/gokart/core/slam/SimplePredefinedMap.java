// code by jph
package ch.ethz.idsc.gokart.core.slam;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** static resource of Dubendorf hangar
 * name refers to png-file in repository, do not rename!
 * 
 * the predefined geometry is crucial for lidar-based localization */
// TODO JPH not necessary to mix localization and obstacles, for instance extrusion only required in one case
public class SimplePredefinedMap implements PredefinedMap {
  /** assume void, i.e. no obstacle, in area outside of image */
  private static final int RGBA_VOID = 0;
  // ---
  /** meter to pixel */
  private final Scalar scale;
  private final BufferedImage bufferedImage;
  private final int width;
  private final int height;
  private final BufferedImage extrudedImage;
  private final ImageRegion imageRegion;
  private final Tensor model2pixel;

  /** @param meter_to_pixel for instance 1[m] may correspond to 7.5 pixel */
  public SimplePredefinedMap(Tensor tensor, BufferedImage bufferedImage, BufferedImage extrudedImage, double meter_to_pixel) {
    this.scale = DoubleScalar.of(meter_to_pixel);
    this.bufferedImage = bufferedImage;
    this.extrudedImage = extrudedImage;
    this.width = bufferedImage.getWidth();
    this.height = bufferedImage.getHeight();
    // ---
    imageRegion = new ImageRegion(tensor, range(), false);
    double s = scale.number().doubleValue();
    double h = bufferedImage.getHeight();
    model2pixel = Tensors.matrix(new Number[][] { //
        { s, 0., 0. }, //
        { 0., -s, h }, //
        { 0., 0., 1. }, //
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
  @Override
  public int getRGB(Point2D point2d) {
    // LONGTERM refactor: bufferedImage, size, color lookup can go in wrapper class?
    int pix = (int) point2d.getX();
    if (0 <= pix && pix < width) {
      int piy = (int) point2d.getY();
      if (0 <= piy && piy < height)
        return bufferedImage.getRGB(pix, piy);
    }
    return RGBA_VOID;
  }

  /** @return image region currently used only for visualization */
  @Override
  public ImageRegion getImageRegion() {
    return imageRegion;
  }

  /** for the dubendorf hangar the range of the map image is
   * {85.333, 85.333} which corresponds to ~85.3 meter width and height
   * 
   * @return */
  @Override
  public Tensor range() {
    return Tensors.vector(width, height).divide(scale);
  }

  @Override // from LocalizationImage
  public Tensor getModel2Pixel() {
    return model2pixel;
  }

  /** @return meter to pixel */
  @Override
  public Scalar scale() {
    return scale;
  }
}
