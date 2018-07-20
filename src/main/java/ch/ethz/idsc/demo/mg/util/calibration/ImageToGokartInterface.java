// code by jph
package ch.ethz.idsc.demo.mg.util.calibration;

/** interface supports only integer values */
// TODO maybe create another interface for double values which are then interpolated
public interface ImageToGokartInterface {
  double[] imageToGokart(int imagePosX, int imagePosY);
}
