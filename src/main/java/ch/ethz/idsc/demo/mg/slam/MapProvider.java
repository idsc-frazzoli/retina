// code by mg
package ch.ethz.idsc.demo.mg.slam;

import java.util.stream.DoubleStream;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

// provides a grid map which is used by the SLAM algorithm
// TODO maybe handle exceptions more elegant
public class MapProvider {
  private final Scalar numberOfCells;
  private final Scalar dimX;
  private final Scalar dimY;
  private final Scalar cellDim;
  private final double[] mapArray;
  private final double cellDim_double;
  private final double cornerX;
  private final double cornerY;
  private final int widthInCells;
  /** tracks max value of values in array */
  private double maxValue;

  MapProvider(SlamConfig slamConfig) {
    dimX = slamConfig.dimX;
    dimY = slamConfig.dimY;
    cellDim = slamConfig.cellDim;
    cellDim_double = cellDim.number().doubleValue();
    numberOfCells = dimX.divide(cellDim).multiply(dimY).divide(cellDim);
    cornerX = slamConfig.corner.Get(0).number().doubleValue();
    cornerY = slamConfig.corner.Get(1).number().doubleValue();
    widthInCells = dimX.divide(cellDim).number().intValue();
    mapArray = new double[numberOfCells.number().intValue()];
    maxValue = 0;
  }

  // the method returns the divided map
  public static void divide(MapProvider numerator, MapProvider denominator, MapProvider targetMap) {
    for (int i = 0; i < targetMap.getNumberOfCells(); i++) {
      if (denominator.getValue(i) == 0) {
        // do nothing
      } else {
        double newValue = numerator.getValue(i) / denominator.getValue(i);
        targetMap.setValue(i, newValue);
      }
    }
  }

  // returns coordinates of cell middle point
  public double[] getCellCoord(int cellIndex) {
    if (cellIndex >= numberOfCells.number().intValue()) {
      System.out.println("Fatal: should not access that");
      return null;
    }
    int gridPosY = cellIndex / widthInCells;
    int gridPosX = cellIndex - gridPosY * widthInCells;
    // TODO precomputation of more values is possible, e.g.: cornerX + 0.5 * cellDim_double
    return new double[] { //
        cornerX + (gridPosX + 0.5) * cellDim_double, //
        cornerY + (gridPosY + 0.5) * cellDim_double };
  }

  // gets the index of the cell in which the coordinate position lies
  // returns numberOfCells if position is outside map
  public int getCellIndex(double posX, double posY) {
    // check if position is inside map
    if (posX <= cornerX || posX >= cornerX + dimX.number().doubleValue() || posY <= cornerY || posY >= cornerY + dimY.number().doubleValue()) {
      // unreasonable number to indicate that we dont have this location
      return numberOfCells.number().intValue();
    }
    int gridPosX = (int) ((posX - cornerX) / cellDim_double);
    int gridPosY = (int) ((posY - cornerY) / cellDim_double);
    return gridPosX + widthInCells * gridPosY;
  }

  /** adds value in grid cell corresponding to pose
   * 
   * @param pose [x,y,angle] pose in world coordinates
   * @param value */
  public void addValue(Tensor pose, double value) {
    addValue( //
        pose.Get(0).number().doubleValue(), //
        pose.Get(1).number().doubleValue(), value);
  }

  /** adds value in grid cell corresponding to pose
   * 
   * @param posX in world coordinates
   * @param posY in world coordinates
   * @param value */
  public void addValue(double posX, double posY, double value) {
    int cellIndex = getCellIndex(posX, posY);
    // case of outside map domain
    if (cellIndex == numberOfCells.number().intValue())
      return;
    mapArray[cellIndex] += value;
    if (mapArray[cellIndex] > maxValue)
      maxValue = mapArray[cellIndex];
  }

  public void addValue(int cellIndex, double value) {
    mapArray[cellIndex] += value;
    if (mapArray[cellIndex] > maxValue)
      maxValue = mapArray[cellIndex];
  }

  public void setValue(int cellIndex, double value) {
    if (value > maxValue)
      maxValue = value;
    mapArray[cellIndex] = value;
  }

  private double getValue(int cellIndex) {
    return mapArray[cellIndex];
  }

  // gets value of cell in which the coordinates are
  public double getValue(Tensor pose) {
    return getValue( //
        pose.Get(0).number().doubleValue(), //
        pose.Get(1).number().doubleValue());
  }

  // gets value of cell in which the coordinates are
  public double getValue(double posX, double posY) {
    int cellIndex = getCellIndex(posX, posY);
    // case of outside map domain
    if (cellIndex == numberOfCells.number().intValue())
      return 0;
    return mapArray[cellIndex];
  }

  // for recorded maps
  public void setMapArray(double[] mapArray) {
    if (this.mapArray.length == mapArray.length) {
      System.arraycopy(mapArray, 0, this.mapArray, 0, this.mapArray.length);
      maxValue = DoubleStream.of(mapArray) //
          .reduce(Math::max).getAsDouble();
    }
  }

  public double[] getMapArray() {
    return mapArray;
  }

  public int getNumberOfCells() {
    return numberOfCells.number().intValue();
  }
  
  public int getWidth() {
    return dimX.divide(cellDim).number().intValue();
  }
  
  public int getHeight() {
    return dimY.divide(cellDim).number().intValue();
  }

  /** @return maxValue or 1 if maxValue == 0
   * since we divide by that value, we avoid all kinds of problems with that */
  public double getMaxValue() {
    if (maxValue == 0)
      return 1;
    return maxValue;
  }
}
