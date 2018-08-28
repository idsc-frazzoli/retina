// code by mg
package ch.ethz.idsc.demo.mg.util.calibration;

import ch.ethz.idsc.tensor.Tensor;

/** interface to compute image plane location that corresponds to provided go kart frame position */
public interface GokartToImageInterface {
  /** @param gokartPosX interpreted as [m] x coordinate in go kart frame
   * @param gokartPosY interpreted as [m] y coordinate in go kart frame
   * @return position in image plane */
  double[] gokartToImage(double gokartPosX, double gokartPosY);

  /** @param gokartPos interpreted as [m] position in go kart frame
   * @return position in image plane */
  Tensor gokartToImage(Tensor gokartPos);
}
