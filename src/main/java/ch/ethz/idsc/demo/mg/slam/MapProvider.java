// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

// provides a grid map which is used by the SLAM algorithm
public class MapProvider {
  private final double[] mapArray;
  private final Scalar dimX;
  private final Scalar dimY;
  private final Scalar cellDim;
  private final Scalar numberOfCells;
  private final double cornerX;
  private final double cornerY;
  private final int xAxisCellNumber;

  MapProvider(PipelineConfig pipelineConfig) {
    dimX = pipelineConfig.dimX;
    dimY = pipelineConfig.dimY;
    cellDim = pipelineConfig.cellDim;
    numberOfCells = dimX.divide(cellDim).multiply(dimY).divide(cellDim);
    cornerX = pipelineConfig.corner.Get(0).number().doubleValue();
    cornerY = pipelineConfig.corner.Get(1).number().doubleValue();
    xAxisCellNumber = dimX.divide(cellDim).number().intValue();
    mapArray = new double[numberOfCells.number().intValue()];
  }

  public static MapProvider divide(MapProvider oneMap, MapProvider anotherMap) {
    MapProvider dividedMap = oneMap;
    for (int i = 0; i < dividedMap.getNumberOfCells(); i++) {
      double newValue;
      if (anotherMap.getValue(i) == 0)
        newValue = oneMap.getValue(i);
      else
        newValue = oneMap.getValue(i) / anotherMap.getValue(i);
      dividedMap.setValue(i, newValue);
    }
    return dividedMap;
  }

  private double getValue(int cellIndex) {
    return mapArray[cellIndex];
  }

  private void setValue(int cellIndex, double value) {
    mapArray[cellIndex] = value;
  }

  // returns coordinates of cell middle point
  public double[] getCellCoord(int cellIndex) {
    if (cellIndex >= numberOfCells.number().intValue()) {
      System.out.println("Fatal: should not access that");
      return null;
    }
    int gridPosY = cellIndex / xAxisCellNumber;
    int gridPosX = cellIndex - gridPosY * xAxisCellNumber;
    double xPos = cornerX + (gridPosX + 0.5) * cellDim.number().doubleValue();
    double yPos = cornerY + (gridPosY + 0.5) * cellDim.number().doubleValue();
    return new double[] { xPos, yPos };
  }

  // gets the index of the cell in which the coordinate position lies
  // returns numberOfCells if position is outside map
  public int getCellIndex(double posX, double posY) {
    // check if position is inside map
    if (posX <= cornerX || posX >= cornerX + dimX.number().doubleValue() || posY <= cornerY || posY >= cornerY + dimY.number().doubleValue()) {
      // unreasonable number to indicate that we dont have this location
      return numberOfCells.number().intValue();
    }
    int gridPosX = (int) ((posX - cornerX) / cellDim.number().doubleValue());
    int gridPosY = (int) ((posY - cornerY) / cellDim.number().doubleValue());
    int cellIndex = gridPosX + xAxisCellNumber * gridPosY;
    return cellIndex;
  }

  public void setValue(Tensor pose, double value) {
    setValue(pose.Get(0).number().doubleValue(), pose.Get(1).number().doubleValue(), value);
  }

  // sets value of cell associated with with given position
  public void setValue(double posX, double posY, double value) {
    int cellIndex = getCellIndex(posX, posY);
    if (cellIndex == numberOfCells.number().intValue()) {
      return;
    }
    mapArray[cellIndex] = value;
  }

  // gets value of cell in which the coordinates are
  public double getValue(Tensor pose) {
    return getValue(pose.Get(0).number().doubleValue(), pose.Get(1).number().doubleValue());
  }

  // gets value of cell in which the coordinates are
  public double getValue(double posX, double posY) {
    int cellIndex = getCellIndex(posX, posY);
    if (cellIndex == numberOfCells.number().intValue()) {
      return 0;
    }
    return mapArray[cellIndex];
  }

  public int getNumberOfCells() {
    return numberOfCells.number().intValue();
  }
}
