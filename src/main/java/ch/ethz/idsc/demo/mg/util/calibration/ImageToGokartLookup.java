// code by mg
package ch.ethz.idsc.demo.mg.util.calibration;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/** lookup table for {@link ImageToGokartUtil} */
public class ImageToGokartLookup implements ImageToGokartInterface {
  public static ImageToGokartLookup fromMatrix(Tensor inputTensor, Scalar unitConversion, int width, int height) {
    ImageToGokartUtil imageToGokartUtil = new ImageToGokartUtil(inputTensor, unitConversion, width);
    return new ImageToGokartLookup(imageToGokartUtil, width, height);
  }

  // ---
  private final ImageToGokartUtil imageToGokartUtil;
  private final double[][] lookupArray;
  private final int width;
  // private final int height;

  private ImageToGokartLookup(ImageToGokartUtil transformUtil, int width, int height) {
    this.width = width;
    // this.height = height;
    lookupArray = new double[width * height][];
    imageToGokartUtil = transformUtil;
    int index = -1;
    for (int y = 0; y < height; ++y)
      for (int x = 0; x < width; ++x)
        lookupArray[++index] = imageToGokartUtil.imageToGokart(x, y);
  }

  @Override // from ImageToGokartInterface
  public double[] imageToGokart(int imagePosX, int imagePosY) {
    int index = imagePosX + imagePosY * width;
    return lookupArray[index];
  }

  @Override // from ImageToGokartInterface
  public Tensor imageToGokartTensor(int index) {
    return Tensors.vectorDouble(lookupArray[index]);
  }

  public void printInfo() {
    imageToGokartUtil.printInfo();
  }
}
