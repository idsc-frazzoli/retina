// code by jph
package ch.ethz.idsc.gokart.core.slam;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.owl.bot.r2.ImageEdges;
import ch.ethz.idsc.owl.bot.r2.ImageRegions;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.io.ResourceData;

public enum ObstaclesMaps {
  /** image of known static obstacles */
  // DUBILAB_OBSTACLES_20180423(7.5), //
  /** image of known static obstacles
   * image with central tents, bus tent, and aerotain */
  // DUBILAB_OBSTACLES_20180610(7.5), //
  /** empty space */
  DUBILAB_20180703(7.5), //
  /** empty space with tents */
  // DUBILAB_OBSTACLES_20180704(7.5), //
  /** empty space */
  DUBILAB_20190314(7.5), //
  ;
  private final PredefinedMap predefinedMap;
  /** number of pixels to extrude geometry for localization */
  private static final int TTL = 3;

  private ObstaclesMaps(double meter_to_pixel) {
    PredefinedMap _predefinedMap = null;
    try {
      Tensor tensor = ImageRegions.grayscale(ResourceData.of(resource()));
      BufferedImage bufferedImage = ImageFormat.of(tensor);
      BufferedImage extrudedImage = ExtrudedImageCache.of(name(), () -> ImageFormat.of(ImageEdges.extrusion(tensor, TTL)));
      _predefinedMap = new SimplePredefinedMap(tensor, bufferedImage, extrudedImage, meter_to_pixel);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    predefinedMap = _predefinedMap;
  }

  public String resource() {
    String name = name();
    int separator = name.indexOf('_');
    return String.format("/%s/obstacles/%s.png", //
        name.substring(0, separator).toLowerCase(), name.substring(separator + 1));
  }

  public PredefinedMap getPredefinedMap() {
    return predefinedMap;
  }
}
