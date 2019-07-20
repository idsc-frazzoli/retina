// code by gjoel
package ch.ethz.idsc.gokart.core.map;

import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ class ErodedMap extends ImageGrid {
  /** @param imageGrid to be eroded
   * @param obstacleRadius with unit SI.METER
   * @return ErodedMap */
  public static ErodedMap of(ImageGrid imageGrid, Scalar obstacleRadius) {
    Tensor rangeCeil = imageGrid.gridSize.multiply(imageGrid.cellDim);
    Dimension dimension = new Dimension(imageGrid.dimX(), imageGrid.dimY());
    return new ErodedMap(imageGrid, imageGrid.lbounds.unmodifiable(), rangeCeil, dimension, obstacleRadius);
  }

  private final ImageGrid imageGrid;
  private final Scalar obsDilationRadius;
  private final Tensor cellDimHalfVec;

  /** @param imageGrid to be eroded
   * @param lbounds vector of length 2
   * @param rangeCeil effective size of grid in coordinate space of the form {value, value}
   * @param dimension of grid in cell space
   * @param obstacleRadius with unit SI.METER */
  private ErodedMap(ImageGrid imageGrid, Tensor lbounds, Tensor rangeCeil, Dimension dimension, Scalar obstacleRadius) {
    super(lbounds, rangeCeil, dimension);
    this.imageGrid = imageGrid;
    obsDilationRadius = Magnitude.METER.apply(obstacleRadius);
    // ---
    cellDimHalfVec = Tensors.of(cellDim, cellDim).divide(RealScalar.of(2)).unmodifiable();
    genObstacleMap();
  }

  /** clears current obstacle image and redraws all known obstacles */
  // TODO LHF this function should return, or update a region object created here, or provided from the outside!
  public void genObstacleMap() {
    imageGraphics.setColor(COLOR_UNKNOWN);
    imageGraphics.fillRect(0, 0, obstacleImage.getWidth(), obstacleImage.getHeight());
    // ---
    if (Scalars.lessEquals(obsDilationRadius, cellDim))
      // FIXME GJOEL/JPH very inefficient, does it make sense at all
      imageGrid.cells() //
          .filter(imageGrid::isCellOccupied) //
          .forEach(cell -> imagePixels[cellToIdx(cell)] = MASK_OCCUPIED);
    else {
      // FIXME GJOEL/JPH draw and read on same object?! -> document
      imageGraphics.setColor(COLOR_OCCUPIED);
      imageGrid.cells() //
          .filter(imageGrid::isCellOccupied).forEach(cell -> {
            Tensor pos = cell.multiply(cellDim).add(cellDimHalfVec);
            drawSphere(pos, obsDilationRadius);
          });
    }
  }

  private void drawSphere(Tensor pos, Scalar radius) {
    // TODO GJOEL/JPH repeated operations when calling function in a loop
    Scalar radiusScaled = radius.multiply(cellDimInv);
    double dim = radiusScaled.number().doubleValue();
    Shape shape = new Ellipse2D.Double( //
        pos.Get(0).multiply(cellDimInv).subtract(radiusScaled).number().doubleValue(), //
        pos.Get(1).multiply(cellDimInv).subtract(radiusScaled).number().doubleValue(), //
        2 * dim, 2 * dim);
    imageGraphics.fill(shape);
  }

  @Override // from OccupancyGrid
  public void clearStart(int startX, int startY, double orientation) {
    // ---
  }
}
