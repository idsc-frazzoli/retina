// code by jph
package ch.ethz.idsc.demo.mg.util.calibration;

import ch.ethz.idsc.tensor.Tensor;

/** interface to compute go kart frame positions based on image plane position. only integer values are supported */
public interface ImageToGokartInterface {
  /** @param imagePosX pixel coordinate
   * @param imagePosY pixel coordinate
   * @return position in go kart frame */
  double[] imageToGokart(int imagePosX, int imagePosY);

  /** @param index = imagePosX + width*imagePosY. standard index when BufferedImage content is represented by 1D array
   * @return position in go kart frame */
  Tensor imageToGokartTensor(int index);
}
