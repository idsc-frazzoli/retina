// code by mg
package ch.ethz.idsc.demo.mg.util;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** precomputes the TransformUtil for integer values of x,y. 
 * TODO how to proceed for float values of x,y? maybe interpolate*/
public class TransformUtilLookup {
  private final TransformUtil transformUtil;
  private final double[] array;
  private final int width;
  private final int height;

  public static TransformUtilLookup fromMatrix(Tensor inputTensor, Scalar unitConversion, Scalar width, Scalar height) {
    return new TransformUtilLookup(inputTensor, unitConversion, width, height);
  }

  public TransformUtilLookup(Tensor inputTensor, Scalar unitConversion, Scalar widthInput, Scalar heightInput) {
    width = widthInput.number().intValue();
    height = heightInput.number().intValue();
    array = new double[2 * width * height];
    transformUtil = new TransformUtil(inputTensor, unitConversion);
    for (int i = 0; i < width * height; i++) {
      int imagePosX = i / height;
      int imagePosY = i - imagePosX * height;
      double[] transformedPoint = transformUtil.imageToWorld(imagePosX, imagePosY);
      array[2 * i] = transformedPoint[0];
      array[2 * i + 1] = transformedPoint[1];
    }
  }

  /** @param imagePosX [pixel]
   * @param imagePosY [pixel]
   * @return physicalCoordinates [m] in gokart reference frame */
  public double[] imageToWorld(int imagePosX, int imagePosY) {
    int index = imagePosX * height + imagePosY;
    return new double[] { array[2 * index], array[2 * index + 1] };
  }

  /** @param imagePosX [pixel]
   * @param imagePosY [pixel]
   * @return physicalCoordinates [m] in gokart reference frame */
  public Tensor imageToWorldTensor(int imagePosX, int imagePosY) {
    int index = imagePosX * height + imagePosY;
    return Tensors.vector(array[2 * index], array[2 * index + 1]);
  }

  public void printInfo() {
    transformUtil.printInfo();
  }
}
