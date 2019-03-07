// code by mg
package ch.ethz.idsc.demo.mg.slam;

import java.util.stream.DoubleStream;

import ch.ethz.idsc.demo.mg.slam.config.SlamCoreConfig;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Tensor;

/** grid map which is used by the SLAM algorithm */
public class MapProvider {
  private final int numberOfCells;
  private final int mapWidth;
  private final int mapHeight;
  private final double cellDim;
  private final double cellDimInv;
  private final double[] mapArray;
  // ---
  /** current lower left corner of map */
  private double cornerXLow;
  private double cornerYLow;
  /** current upper right corner of map */
  private double cornerXHigh;
  private double cornerYHigh;
  /** tracks max value of values in array */
  private double maxValue;

  public MapProvider(SlamCoreConfig slamCoreConfig) {
    cellDim = Magnitude.METER.toDouble(slamCoreConfig.cellDim);
    cellDimInv = 1 / cellDim;
    mapWidth = slamCoreConfig.mapWidth();
    mapHeight = slamCoreConfig.mapHeight();
    numberOfCells = mapWidth * mapHeight;
    cornerXLow = Magnitude.METER.toDouble(slamCoreConfig.corner.Get(0));
    cornerYLow = Magnitude.METER.toDouble(slamCoreConfig.corner.Get(1));
    Tensor cornerHigh = slamCoreConfig.cornerHigh();
    cornerXHigh = Magnitude.METER.toDouble(cornerHigh.Get(0));
    cornerYHigh = Magnitude.METER.toDouble(cornerHigh.Get(1));
    mapArray = new double[numberOfCells];
    maxValue = 0;
    /** when in localization mode, we load a prior map */
    if (slamCoreConfig.slamAlgoConfig == SlamAlgoConfig.localizationMode)
      setMapArray(slamCoreConfig.getMapArray());
  }

  /** divides the provided maps and saves into targetMap
   * 
   * @param numerator
   * @param denominator
   * @param targetMap */
  // currently unused, requires normalization map
  public static void divide(MapProvider numerator, MapProvider denominator, MapProvider targetMap) {
    // TODO MG loop can be done in parallel
    for (int index = 0; index < targetMap.getNumberOfCells(); ++index)
      if (denominator.getValue(index) != 0) {
        double newValue = numerator.getValue(index) / denominator.getValue(index);
        targetMap.setValue(index, newValue);
      }
  }

  private void findMaxValue() {
    maxValue = DoubleStream.of(mapArray) //
        .reduce(Math::max).getAsDouble();
  }

  /** moves the map by the positionDifference vector. The whole mapArray is updated
   * 
   * @param positionDifference unitless */
  public void moveMap(Tensor positionDifference) {
    double[] mapArray = new double[numberOfCells];
    for (int i = 0; i < numberOfCells; i++) {
      double[] newCoord = getCellCoord(i);
      newCoord[0] += positionDifference.Get(0).number().doubleValue();
      newCoord[1] += positionDifference.Get(1).number().doubleValue();
      mapArray[i] = getValue(newCoord[0], newCoord[1]);
    }
    updateCorners(positionDifference);
    setMapArray(mapArray);
  }

  /** @param positionDifference unitless */
  private void updateCorners(Tensor positionDifference) {
    cornerXLow += positionDifference.Get(0).number().doubleValue();
    cornerXHigh += positionDifference.Get(0).number().doubleValue();
    cornerYLow += positionDifference.Get(1).number().doubleValue();
    cornerYHigh += positionDifference.Get(1).number().doubleValue();
  }

  /** @return coordinates of cell middle point */
  public double[] getCellCoord(int cellIndex) {
    if (cellIndex >= numberOfCells) {
      System.out.println("FATAL: should not access that");
      return null;
    }
    int gridPosY = cellIndex / mapWidth;
    int gridPosX = cellIndex - gridPosY * mapWidth;
    // TODO JPH precomputation of more values is possible, e.g.: cornerX + 0.5 * cellDim
    return new double[] { //
        cornerXLow + (gridPosX + 0.5) * cellDim, //
        cornerYLow + (gridPosY + 0.5) * cellDim };
  }

  /** @param posX [m]
   * @param posY [m]
   * @return cellIndex or -1 if position is outside map */
  public int getCellIndex(double posX, double posY) {
    // check if position is inside map
    if (posX <= cornerXLow || posX >= cornerXHigh || posY <= cornerYLow || posY >= cornerYHigh) {
      // unreasonable number to indicate that we don't have this location
      return -1;
    }
    int gridPosX = (int) ((posX - cornerXLow) * cellDimInv);
    int gridPosY = (int) ((posY - cornerYLow) * cellDimInv);
    return gridPosX + mapWidth * gridPosY;
  }

  /** adds value in grid cell corresponding to coordinates. does nothing if pose is outside map domain
   * 
   * @param worldCoord world frame map position
   * @param value >= 0 */
  public void addValue(Tensor worldCoord, double value) {
    addValue( //
        worldCoord.Get(0).number().doubleValue(), //
        worldCoord.Get(1).number().doubleValue(), value);
  }

  /** adds value in grid cell corresponding to pose. does nothing if pose is outside map domain
   * 
   * @param posX world frame
   * @param posY world frame
   * @param value >= 0 */
  public void addValue(double posX, double posY, double value) {
    int cellIndex = getCellIndex(posX, posY);
    // case of outside map domain
    if (cellIndex == -1)
      return;
    mapArray[cellIndex] += value;
    if (mapArray[cellIndex] > maxValue)
      maxValue = mapArray[cellIndex];
  }

  /** @param cellIndex
   * @param value >= 0 */
  public void addValue(int cellIndex, double value) {
    mapArray[cellIndex] += value;
    if (mapArray[cellIndex] > maxValue)
      maxValue = mapArray[cellIndex];
  }

  public void setValue(int cellIndex, double value) {
    if (mapArray[cellIndex] == maxValue && value < maxValue) {
      mapArray[cellIndex] = value;
      findMaxValue();
    }
    if (value > maxValue)
      maxValue = value;
    mapArray[cellIndex] = value;
  }

  private double getValue(int cellIndex) {
    return mapArray[cellIndex];
  }

  /** value of grid cell which correspond to worldCoord
   * 
   * @param worldCoord world frame
   * @return value at the position of 0 if coordinates are outside map domain */
  public double getValue(Tensor worldCoord) {
    return getValue( //
        worldCoord.Get(0).number().doubleValue(), //
        worldCoord.Get(1).number().doubleValue());
  }

  /** @param posX world frame x coordinate
   * @param posY world frame y coordinate
   * @return value at the position or 0 if coordinates are outside map domain */
  public double getValue(double posX, double posY) {
    int cellIndex = getCellIndex(posX, posY);
    // case of outside map domain
    if (cellIndex == -1)
      return 0;
    return mapArray[cellIndex];
  }

  /** @param mapArray must have same length as original mapArray */
  public void setMapArray(double[] mapArray) {
    if (this.mapArray.length == mapArray.length) {
      System.arraycopy(mapArray, 0, this.mapArray, 0, this.mapArray.length);
      findMaxValue();
    } else
      throw new IllegalArgumentException();
  }

  public double[] getMapArray() {
    return mapArray;
  }

  public int getNumberOfCells() {
    return numberOfCells;
  }

  public int getMapWidth() {
    return mapWidth;
  }

  public int getMapHeight() {
    return mapHeight;
  }

  /** @return x coordinate of lower left corner of map */
  public double getCornerX() {
    return cornerXLow;
  }

  /** @return y coordinate of lower left corner of map */
  public double getCornerY() {
    return cornerYLow;
  }

  /** @return maxValue or 1 if maxValue == 0
   * since we divide by that value, we avoid all kinds of problems with that */
  public double getMaxValue() {
    return maxValue == 0 ? 1 : maxValue;
  }
}
