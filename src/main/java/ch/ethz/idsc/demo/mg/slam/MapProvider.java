// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;
import ch.ethz.idsc.tensor.Tensor;

// provides a grid map which is used by the SLAM algorithm
public class MapProvider {
  private final double[] mapArray;
  private final int dimX;
  private final int dimY;
  private final double cellDim;
  private final int cellNumber;

  MapProvider(PipelineConfig pipelineConfig) {
    mapArray = null;
    dimX = pipelineConfig.dimX.number().intValue();
    dimY = pipelineConfig.dimY.number().intValue();
    cellDim = pipelineConfig.cellDim.number().doubleValue();
    cellNumber = dimX * dimY;
  }

  public static MapProvider divide(MapProvider firstMap, MapProvider anotherMap) {
    MapProvider dividedMap = firstMap;
    for (int i = 0; i < dividedMap.getCellNumber(); i++) {
      double newValue = firstMap.getValue(i) / anotherMap.getValue(i);
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

  public int getCellNumber() {
    return cellNumber;
  }

  // gets the index of the cell in which the coordinate position lies
  public int getCell(double posX, double posY) {
    // ..
    return 0;
  }

  public int getCell(Tensor pose) {
    return getCell(pose.Get(0).number().doubleValue(), pose.Get(1).number().doubleValue());
  }

  // sets value of cell associated with with given position
  public void setValue(double posX, double posY, double value) {
    int cellIndex = getCell(posX, posY);
    mapArray[cellIndex] = value;
  }

  public void setValue(Tensor pose, double value) {
    int cellIndex = getCell(pose);
    mapArray[cellIndex] = value;
  }

  // gets value of cell in which the coordinates are
  public double getValue(Tensor pose) {
    return getValue(pose.Get(0).number().doubleValue(), pose.Get(1).number().doubleValue());
  }

  // gets value of cell in which the coordinates are
  public double getValue(double posX, double posY) {
    int cellIndex = getCell(posX, posY);
    return mapArray[cellIndex];
  }
}
