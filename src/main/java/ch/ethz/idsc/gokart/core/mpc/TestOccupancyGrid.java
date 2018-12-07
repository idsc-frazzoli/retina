package ch.ethz.idsc.gokart.core.mpc;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import ch.ethz.idsc.subare.util.UserHome;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Import;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;

public class TestOccupancyGrid implements PlanableOccupancyGrid {
  BufferedImage img = null;
  int m;
  int n;

  public TestOccupancyGrid() {
    try {
      // grid = Import.of();
      img = ImageIO.read(UserHome.file("Documents/bigOccupancyGrid.png"));
    } catch (IOException e) {
      System.err.println("could not load image");
    }
    m = img.getWidth();
    n = img.getHeight();
  }

  @Override
  public Tensor getGridSize() {
    return Tensors.vector(m, n);
  }

  @Override
  public boolean isCellOccupied(Point points) {
    if (points.x >= 0 && points.x < m && //
        points.y >= 0 && points.y < n) {
      int p = img.getRGB(points.x, points.y);
      int r = (p >> 16) & 0xff;
      return r > 100;
    } else
      return true;
  }

  @Override
  public Tensor getTransform() {
    return IdentityMatrix.of(3);
  }

  @Override
  public boolean isMember(Tensor element) {
    Point point = new Point(element.Get(0).number().intValue(), element.Get(1).number().intValue());
    return isCellOccupied(point);
  }
}
