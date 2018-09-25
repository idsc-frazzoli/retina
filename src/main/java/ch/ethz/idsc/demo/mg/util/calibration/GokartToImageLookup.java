// code by mg
package ch.ethz.idsc.demo.mg.util.calibration;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

// provides a lookup table for the world to image transform
// idea: create lookup table for a rectangular area in front of the go kart that encloses the field of view as
// seen by the ImageTogokartUtil. Discretize with the cellDim parameter. Include interpolation method.
// TODO unused and unfinished code
public class GokartToImageLookup implements GokartToImageInterface {
  public static GokartToImageLookup fromMatrix(Tensor inputTensor, Scalar unitConversion, Scalar cellDim, Scalar lookAheadDistance, int width) {
    return new GokartToImageLookup(new ImageToGokartUtil(inputTensor, unitConversion, width), //
        new GokartToImageUtil(inputTensor, unitConversion), cellDim, lookAheadDistance);
  }

  // ---
  private final GokartToImageUtil gokartToImageUtil;
  private final double[] lookupArray;
  private final double cellDim;
  private final double lookAhead;
  private final double padding = 1; // [m] doesnt matter if lookuptable is too large
  private final int widthInCells;
  private final int heightInCells;

  public GokartToImageLookup(ImageToGokartUtil imageToGokartUtil, GokartToImageUtil gokartToImageUtil, Scalar cellDimension, Scalar lookAheadDistance) {
    this.gokartToImageUtil = gokartToImageUtil;
    cellDim = cellDimension.number().doubleValue();
    // lookAhead = lookAheadDistance.number().doubleValue();
    lookAhead = 20;
    // find the left and right corners at lookAheadDistance
    double[] lookAheadImagePlane = gokartToImageUtil.gokartToImage(lookAhead, 0);
    double[] upperLeft = imageToGokartUtil.imageToGokart(0, lookAheadImagePlane[1]);
    double[] upperRight = imageToGokartUtil.imageToGokart(239, lookAheadImagePlane[1]);
    double[] lowerLeft = imageToGokartUtil.imageToGokart(0, 179);
    // compute lookup array dimensions
    double rectangleWidth = upperLeft[1] + 2 * padding - upperRight[1];
    double rectangleHeight = upperLeft[0] + 2 * padding - lowerLeft[0];
    widthInCells = (int) (rectangleWidth / cellDim);
    heightInCells = (int) (rectangleHeight / cellDim);
    lookupArray = new double[2 * widthInCells * heightInCells];
    int index = 0;
    for (int i = 0; i < widthInCells; i++) {
      for (int j = 0; j < heightInCells; j++) {
        double gokartPosX = upperLeft[0] + padding;
        double gokartPosY = upperLeft[1] + padding;
        double[] transformedPoint = this.gokartToImageUtil.gokartToImage(gokartPosX, gokartPosY);
        lookupArray[2 * index] = transformedPoint[0];
        lookupArray[2 * index + 1] = transformedPoint[1];
        index++;
      }
    }
  }

  @Override // from GokartToImageInterface
  public double[] gokartToImage(double gokartPosX, double gokartPosY) {
    // find nearest position for which we have a lookup value
    // then return that value
    return null;
  }

  @Override // from GokartToImageInterface
  public Tensor gokartToImage(Tensor gokartPos) {
    throw new RuntimeException();
  }
}
