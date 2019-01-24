// code by mh
package ch.ethz.idsc.gokart.core.map;

import java.awt.Point;
import java.awt.image.BufferedImage;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;

/* package */ class TestOccupancyGrid implements PlanableOccupancyGrid {
  private static final Tensor ID3 = IdentityMatrix.of(3).unmodifiable();
  // ---
  final BufferedImage img;
  final int m;
  final int n;

  public TestOccupancyGrid(BufferedImage img) {
    this.img = img;
    m = img.getWidth();
    n = img.getHeight();
  }

  @Override
  public Tensor getGridSize() {
    return Tensors.vector(m, n);
  }

  @Override
  public boolean isCellOccupied(int x, int y) {
    if (x >= 0 && x < m && //
        y >= 0 && y < n) {
      int p = img.getRGB(x, y);
      int r = (p >> 16) & 0xff;
      return r > 100;
    }
    return true;
  }

  @Override
  public Tensor getTransform() {
    return ID3;
  }

  @Override
  public boolean isMember(Tensor element) {
    Point point = new Point(element.Get(0).number().intValue(), element.Get(1).number().intValue());
    return isCellOccupied(point.x, point.y);
  }
}
