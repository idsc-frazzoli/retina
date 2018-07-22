// code by mg
package ch.ethz.idsc.demo.mg.util;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** precomputes the TransformUtil for integer values of x, y.
 * TODO how to proceed for float values of x,y? maybe interpolate */
public class ImageToGokartLookup implements ImageToGokartInterface {
  public static ImageToGokartLookup fromMatrix(Tensor inputTensor, Scalar unitConversion, Scalar width, Scalar height) {
    return new ImageToGokartLookup(new ImageToGokartUtil(inputTensor, unitConversion), width, height);
  }

  // ---
  private final ImageToGokartUtil transformUtil;
  private final double[] lookupArray;
  private final int width;
  private final int height;

  private ImageToGokartLookup(ImageToGokartUtil transformUtil, Scalar widthInput, Scalar heightInput) {
    width = widthInput.number().intValue();
    height = heightInput.number().intValue();
    lookupArray = new double[2 * width * height];
    this.transformUtil = transformUtil;
    int index = 0;
    for (int y = 0; y < height; ++y)
      for (int x = 0; x < width; ++x) {
        double[] transformedPoint = this.transformUtil.imageToGokart(x, y);
        lookupArray[2 * index] = transformedPoint[0];
        lookupArray[2 * index + 1] = transformedPoint[1];
        index++;
      }
  }

  /** @param imagePosX [pixel]
   * @param imagePosY [pixel]
   * @return physicalCoordinates [m] in gokart reference frame */
  @Override
  public double[] imageToGokart(int imagePosX, int imagePosY) {
    int index = imagePosX + imagePosY * width;
    index <<= 1;
    return new double[] { lookupArray[index], lookupArray[index + 1] };
  }

  /** @param index of pixel
   * @return physicalCoordinates in units [m] in gokart reference frame */
  public Tensor imageToGokartTensor(int index) {
    index <<= 1;
    return Tensors.vector(lookupArray[index], lookupArray[index + 1]);
  }

  public void printInfo() {
    transformUtil.printInfo();
  }
}
