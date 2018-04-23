// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.image.BufferedImage;
import java.io.File;

import ch.ethz.idsc.owl.bot.r2.ImageEdges;
import ch.ethz.idsc.owl.bot.r2.ImageRegions;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.io.ResourceData;

/** static resource of Dubendorf hangar
 * name refers to png-file in repository, do not rename!
 * 
 * the predefined geometry is crucial for lidar-based localization */
public enum PredefinedMap {
  /** dubendorf hangar map version 20180122 */
  DUBENDORF_HANGAR_20180122(7.5), //
  /** dubendorf hangar map version 20180423
   * features the outside fence visible when the hangar doors are open */
  DUBENDORF_HANGAR_20180423(7.5), //
  ;
  /** number of pixels to extrude geometry */
  public static final int TTL = 3;
  // ---
  private final Scalar scale;
  private final BufferedImage bufferedImage;
  private final int size;
  private final BufferedImage extrudedImage;
  private final ImageRegion imageRegion;

  /** @param meter_to_pixel for instance 1[m] may correspond to 7.5 pixel */
  private PredefinedMap(double meter_to_pixel) {
    this.scale = DoubleScalar.of(meter_to_pixel);
    String string = String.format("/map/%s.png", name().replace('_', File.separatorChar).toLowerCase());
    Tensor tensor = ImageRegions.grayscale(ResourceData.of(string));
    bufferedImage = ImageFormat.of(tensor);
    this.size = bufferedImage.getWidth();
    extrudedImage = ImageFormat.of(ImageEdges.extrusion(tensor, TTL));
    imageRegion = new ImageRegion(tensor, range(), false);
    // ---
    if (bufferedImage.getHeight() != size)
      new RuntimeException("map image not squared").printStackTrace();
  }

  /** the map for visualization is strictly black and white
   * 
   * @return instance of map used for visualization */
  public BufferedImage getImage() {
    return bufferedImage;
  }

  /** the map for localization has grayscale values in the proximity of static geometry
   * 
   * @return instance of extruded map used for lidar-based localization */
  public BufferedImage getImageExtruded() {
    return extrudedImage;
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
}
