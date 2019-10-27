// code by jph
package ch.ethz.idsc.gokart.core.slam;

import java.awt.geom.Point2D;

import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** static resource of Dubendorf hangar
 * name refers to png-file in repository, do not rename!
 * 
 * the predefined geometry is crucial for lidar-based localization */
public interface PredefinedMap extends LocalizationImage {
  /** @param point2d
   * @return color value at pixel in given location */
  int getRGB(Point2D point2d);

  /** @return image region currently used only for visualization */
  ImageRegion getImageRegion();

  /** for the dubendorf hangar the range of the map image is
   * {85.333, 85.333} which corresponds to ~85.3 meter width and height
   * 
   * @return */
  Tensor range();

  /** @return meter to pixel */
  Scalar scale();
  // public static void main(String[] args) throws IOException {
  // Tensor tensor = ImageRegions.grayscale(Import.of(HomeDirectory.Pictures("rieter_hall_bw_crop.png")));
  // BufferedImage bufferedImage = ImageFormat.of(tensor);
  // ImageIO.write(bufferedImage, "png", HomeDirectory.Pictures("20191022.png"));
  // }
}
