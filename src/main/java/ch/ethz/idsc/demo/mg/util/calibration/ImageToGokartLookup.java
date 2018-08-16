// code by mg
package ch.ethz.idsc.demo.mg.util.calibration;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** lookup table for {@link ImageToGokartUtil} */
// TODO implement interpolation to handle floating input values
public class ImageToGokartLookup implements ImageToGokartInterface {
  public static ImageToGokartLookup fromMatrix(Tensor inputTensor, Scalar unitConversion, Scalar width, Scalar height) {
    ImageToGokartUtil imageToGokartUtil = new ImageToGokartUtil(inputTensor, unitConversion, width);
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

  // from ImageToGokartInterface
  @Override
  public double[] imageToGokart(int imagePosX, int imagePosY) {
    int index = imagePosX + imagePosY * width;
    return lookupArray[index];
  }

  // from ImageToGokartInterface
  @Override
  public Tensor imageToGokartTensor(int index) {
    return Tensors.vectorDouble(lookupArray[index]);
  }

  public void printInfo() {
    transformUtil.printInfo();
  }
}
