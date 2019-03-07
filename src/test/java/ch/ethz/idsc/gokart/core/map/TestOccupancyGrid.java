// code by mh
package ch.ethz.idsc.gokart.core.map;

import java.awt.image.BufferedImage;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;

/* package */ class TestOccupancyGrid implements OccupancyGrid {
  private static final Tensor ID3 = IdentityMatrix.of(3).unmodifiable();
  // ---
  private final BufferedImage bufferedImage;
  private final int m;
  private final int n;

  public TestOccupancyGrid(BufferedImage bufferedImage) {
    this.bufferedImage = bufferedImage;
    m = bufferedImage.getWidth();
    n = bufferedImage.getHeight();
  }

  @Override // from OccupancyGrid
  public Tensor getGridSize() {
    return Tensors.vector(m, n);
  }

  @Override // from OccupancyGrid
  public boolean isCellOccupied(int x, int y) {
    if (x >= 0 && x < m && //
        y >= 0 && y < n) {
      int p = bufferedImage.getRGB(x, y);
      int r = (p >> 16) & 0xff;
      return r > 100;
    }
    return true;
  }

  @Override // from OccupancyGrid
  public Tensor getTransform() {
    return ID3;
  }

  @Override // from OccupancyGrid
  public boolean isMember(Tensor element) {
    return isCellOccupied( //
        element.Get(0).number().intValue(), //
        element.Get(1).number().intValue());
  }
}
