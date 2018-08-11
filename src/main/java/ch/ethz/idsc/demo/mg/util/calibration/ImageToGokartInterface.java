// code by jph
package ch.ethz.idsc.demo.mg.util.calibration;

import ch.ethz.idsc.tensor.Tensor;

/** interface supports only integer values */
public interface ImageToGokartInterface {
  /** .
   * @param imagePosX pixel coordinate
   * @param imagePosY pixel coordinate
   * @return double array of length 2 in gokart coordinates */
  double[] imageToGokart(int imagePosX, int imagePosY);

  Tensor imageToGokartTensor(int index);
}
