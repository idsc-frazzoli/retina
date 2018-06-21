// code by mg
package ch.ethz.idsc.demo.mg.util;

import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

// provides a lookup table for the world to image transform
// idea: create lookup table for a rectangular area in front of the go kart that encloses the field of view as
// seen by the ImageTogokartUtil. Discretize with the cellDim parameter. Include interpolation method.
public class GokartToImageLookup implements GokartToImageInterface {
  public static GokartToImageLookup fromMatrix(Tensor inputTensor, Scalar unitConversion, Scalar cellDim, Scalar lookAheadDistance) {
    return new GokartToImageLookup(new ImageToGokartUtil(inputTensor, unitConversion), new GokartToImageUtil(inputTensor, unitConversion), cellDim,
        lookAheadDistance);
  }

  private final GokartToImageUtil gokartToImageUtil;
  private final ImageToGokartUtil imageToGokartUtil;
  // private final double[] lookupArray;
  private final double lookAhead;
  private final double padding = 1; // [m] doesnt matter if lookuptable is too large

  public GokartToImageLookup(ImageToGokartUtil imageToGokartUtil, GokartToImageUtil gokartToImageUtil, Scalar cellDim, Scalar lookAheadDistance) {
    this.gokartToImageUtil = gokartToImageUtil;
    this.imageToGokartUtil = imageToGokartUtil;
    // lookAhead = lookAheadDistance.number().doubleValue();
    lookAhead = 20;
    // find the left and right corners at lookAheadDistance
    double[] lookAheadImagePlane = gokartToImageUtil.gokartToImage(lookAhead, 0);
    double[] upperLeft = imageToGokartUtil.imageToGokart(0, lookAheadImagePlane[1]);
    double[] upperRight = imageToGokartUtil.imageToGokart(239, lookAheadImagePlane[1]);
    double[] lowerLeft = imageToGokartUtil.imageToGokart(0, 179);
    // ..
    double rectangleWidth = upperLeft[1] + 2 * padding - upperRight[1];
    double rectangleHeight = upperLeft[0] + 2 * padding - lowerLeft[0];
    // once we know the corners, generate lookupArray of appropriate size
    // store values of transformation of center of the cells
  }

  @Override
  public double[] gokartToImage(double worldPosX, double worldPosY) {
    // find nearest position for which we have a lookup value
    // then return that value
    return null;
  }

  // testing
  public static void main(String[] args) {
    PipelineConfig pipelineConfig = new PipelineConfig();
    GokartToImageLookup test = pipelineConfig.createGokartToImageLookup();
  }
}
