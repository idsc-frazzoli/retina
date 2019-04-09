// code by gjoel
package ch.ethz.idsc.gokart.core.map;

import java.awt.Dimension;
import java.awt.geom.Path2D;
import java.util.Objects;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.sca.Ceiling;
import ch.ethz.idsc.tensor.sca.Sign;

public class SightLineOccupancyGrid extends ImageGrid {
  /** @param lbounds vector of length 2
   * @param range effective size of grid in coordinate space
   * @param cellDim non-negative dimension of cell with unit SI.METER
   * @return SightLineOccupancyGrid with grid dimensions ceil'ed to fit a whole number of cells per dimension */
  public static SightLineOccupancyGrid of(Tensor lbounds, Tensor range, Scalar cellDim) {
    // sizeCeil is for instance {200[m^-1], 200[m^-1]}
    Tensor sizeCeil = Ceiling.of(range.divide(Sign.requirePositive(cellDim)));
    Tensor rangeCeil = sizeCeil.multiply(cellDim);
    Dimension dimension = new Dimension( //
        Magnitude.PER_METER.toInt(sizeCeil.Get(0)), //
        Magnitude.PER_METER.toInt(sizeCeil.Get(1)));
    return new SightLineOccupancyGrid(lbounds, rangeCeil, dimension);
  }

  /** @param lbounds vector of length 2
   * @param rangeCeil effective size of grid in coordinate space of the form {value, value}
   * @param dimension of grid in cell space */
  private SightLineOccupancyGrid(Tensor lbounds, Tensor rangeCeil, Dimension dimension) {
    super(lbounds, rangeCeil, dimension);
  }

  /** first clear visible free space and then redraw new obstacles
   * @param polygon Tensor */
  public synchronized void updateMap(Tensor polygon) {
    if (Objects.nonNull(gokart2world)) {
      freeSpace(polygon);
      obstacles(polygon);
    }
  }

  /** clear visible free space
   * @param polygon Tensor */
  private void freeSpace(Tensor polygon) {
    imageGraphics.setColor(COLOR_UNKNOWN);
    Path2D path2D = lidar2cellLayer.toPath2D(polygon);
    path2D.closePath();
    imageGraphics.fill(path2D);
  }

  /** redraw new obstacles
   * @param polygon Tensor */
  private void obstacles(Tensor polygon) {
    imageGraphics.setColor(COLOR_OCCUPIED);
    polygon.forEach(point -> {
      // TODO JG/JPH this filtering should happen elsewhere
      if (!point.equals(Array.zeros(2))) {
        Tensor cell = lidarToCell(point);
        if (isCellInGrid(cell))
          imagePixels[cellToIdx(cell)] = MASK_OCCUPIED;
      }
    });
  }

  @Override // from OccupancyGrid
  public void clearStart(int startX, int startY, double orientation) {
    // ---
  }
}
