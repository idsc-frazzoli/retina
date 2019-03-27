package ch.ethz.idsc.gokart.core.map;

import java.awt.Graphics2D;

import ch.ethz.idsc.gokart.core.perc.SpacialXZObstaclePredicate;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;

public abstract class AbstractMapping<T extends ImageGrid> extends AbstractLidarMapping implements //
    OccupancyGrid, RenderInterface {
  protected final T occupancyGrid;

  /* package */ AbstractMapping(T occupancyGrid, SpacialXZObstaclePredicate predicate, int waitMillis) {
    super(predicate, waitMillis);
    this.occupancyGrid = occupancyGrid;
  }

  @Override // from OccupancyGrid
  public Tensor getGridSize() {
    return occupancyGrid.getGridSize();
  }

  @Override // from OccupancyGrid
  public boolean isCellOccupied(int pix, int piy) {
    return occupancyGrid.isCellOccupied(pix, piy);
  }

  @Override // from OccupancyGrid
  public Tensor getTransform() {
    return occupancyGrid.getTransform();
  }

  @Override // from OccupancyGrid
  public void clearStart(int startX, int startY, double orientation) {
    occupancyGrid.clearStart(startX, startY, orientation);
  }

  @Override // from OccupancyGrid
  public boolean isMember(Tensor state) {
    return occupancyGrid.isMember(state);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    occupancyGrid.render(geometricLayer, graphics);
  }

  public abstract void prepareMap();

  public abstract ImageGrid getMap();
}
