// code by mg
package ch.ethz.idsc.demo.mg.util.calibration;

import ch.ethz.idsc.tensor.Tensor;

public interface GokartToImageInterface {
  double[] gokartToImage(double gokartPosX, double gokartPosY);

  Tensor gokartToImage(Tensor gokartPos);
}
