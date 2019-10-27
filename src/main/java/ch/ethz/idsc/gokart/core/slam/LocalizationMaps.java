// code by jph
package ch.ethz.idsc.gokart.core.slam;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.owl.bot.r2.ImageEdges;
import ch.ethz.idsc.owl.bot.r2.ImageRegions;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.io.ResourceData;

/** static resource of Dubendorf hangar
 * name refers to png-file in repository, do not rename!
 * 
 * the predefined geometry is crucial for lidar-based localization */
// TODO JPH not necessary to mix localization and obstacles, for instance extrusion only required in one case
public enum LocalizationMaps {
  /** image, no tents, no aerotain */
  // DUBILAB_20180506(7.5), //
  // DUBILAB_20180702(7.5), //
  /** only the first tent */
  // DUBILAB_20180705(7.5), //
  /** only front tent, four balloons in the aerotain area */
  // DUBILAB_20180813(7.5), //
  /** only front tent, aerotain poster shifted, no balloons */
  DUBILAB_20180901(7.5), //
  /** no tents, aerotain poster shifted, no balloons */
  // DUBILAB_20180904(7.5), //
  /** car and house tents in new positions for tse2 planning */
  // DUBILAB_20180912(7.5), //
  /** tents at parking position */
  DUBILAB_20181128(7.5), //
  // DUBILAB_20190307(7.5), //
  /** more details in swiss loop area */
  DUBILAB_20190309(7.5), //
  /** dust proof wall */
  DUBILAB_20190314(7.5), //
  /** dust proof wall */
  DUBILAB_20190708(7.5), //
  /** rieter hall */
  RIETER_20191022(7.5), //
  ;
  private final PredefinedMap predefinedMap;
  /** number of pixels to extrude geometry for localization */
  private static final int TTL = 3;

  private LocalizationMaps(double meter_to_pixel) {
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
    return String.format("/%s/localization/%s.png", //
        name.substring(0, separator).toLowerCase(), name.substring(separator + 1));
  }

  public PredefinedMap getPredefinedMap() {
    return predefinedMap;
  }
}
