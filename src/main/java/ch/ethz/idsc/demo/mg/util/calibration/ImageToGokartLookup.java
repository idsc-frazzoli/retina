// code by mg
package ch.ethz.idsc.demo.mg.util.calibration;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** precomputes the TransformUtil for integer values of x, y */
// TODO how to proceed for float values of x,y? maybe interpolate
public class ImageToGokartLookup implements ImageToGokartInterface {
  /** @param inputTensor of length 6 where first 3 rows have length 3, the last 3 rows have length 2
   * @param unitConversion for example 1000
   * @param width for instance 240
   * @param height for instance 180
   * @return */
  public static ImageToGokartLookup fromMatrix(Tensor inputTensor, Scalar unitConversion, Scalar width, Scalar height) {
    ImageToGokartUtil imageToGokartUtil = new ImageToGokartUtil(inputTensor, unitConversion);
    return new ImageToGokartLookup(imageToGokartUtil, width, height);
  }

  // ---
  private final ImageToGokartUtil transformUtil;
  private final double[][] lookupArray;
  private final int width;
  private final int height;

  private ImageToGokartLookup(ImageToGokartUtil transformUtil, Scalar width, Scalar height) {
    this.width = width.number().intValue();
    this.height = height.number().intValue();
    lookupArray = new double[this.width * this.height][];
    this.transformUtil = transformUtil;
    int index = -1;
    for (int y = 0; y < this.height; ++y)
      for (int x = 0; x < this.width; ++x)
        lookupArray[++index] = this.transformUtil.imageToGokart(x, y);
  }

  /** @param imagePosX [pixel]
   * @param imagePosY [pixel]
   * @return physicalCoordinates [m] in go kart reference frame */
  @Override
  public double[] imageToGokart(int imagePosX, int imagePosY) {
    int index = imagePosX + imagePosY * width;
    return lookupArray[index];
  }

  /** @param index of pixel
   * @return physicalCoordinates [m] in go kart reference frame */
  @Override
  public Tensor imageToGokartTensor(int index) {
    return Tensors.vectorDouble(lookupArray[index]);
  }

  public void printInfo() {
    transformUtil.printInfo();
  }
}
