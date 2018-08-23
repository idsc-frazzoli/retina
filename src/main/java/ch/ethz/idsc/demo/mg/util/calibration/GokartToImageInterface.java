// code by mg
package ch.ethz.idsc.demo.mg.util.calibration;

import ch.ethz.idsc.tensor.Tensor;

/** interface to compute image plane location that corresponds to provided go kart frame position */
public interface GokartToImageInterface {
  // TODO MG document functions of public interface
  double[] gokartToImage(double gokartPosX, double gokartPosY);

  Tensor gokartToImage(Tensor gokartPos);
}
