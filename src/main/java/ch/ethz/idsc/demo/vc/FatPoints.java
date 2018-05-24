// code by vc
package ch.ethz.idsc.demo.vc;

import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;

public class FatPoints {
  private static final GeometricLayer IDENTITY_LAYER = GeometricLayer.of(IdentityMatrix.of(3));
  // ---
  private final List<Tensor> collectionOfAreas = new ArrayList<>();
  private double totalArea;

  public FatPoints(Tensor points, double w) {
    totalArea = points.length() * w * w;
    for (Tensor x : points)
      addToCollection(x, w);
  }

  public FatPoints(Tensor hulls) {
    for (Tensor hull : hulls) {
      collectionOfAreas.add(hull);
      totalArea = totalArea + computeBetterArea(hull);
    }
  }

  // private since function does not update totalArea
  private void addToCollection(Tensor point, double w) {
    double xCenter = point.Get(0).number().doubleValue();
    double yCenter = point.Get(1).number().doubleValue();
    Tensor s = Tensors.empty();
    s.append(Tensors.vector(xCenter - w / 2, yCenter + w / 2));
    s.append(Tensors.vector(xCenter + w / 2, yCenter + w / 2));
    s.append(Tensors.vector(xCenter + w / 2, yCenter - w / 2));
    s.append(Tensors.vector(xCenter - w / 2, yCenter - w / 2));
    collectionOfAreas.add(s);
  }

  public double computeArea(Area area) {
    Rectangle2D rectangle2d = area.getBounds2D();
    return rectangle2d.getWidth() * rectangle2d.getHeight();
  }

  public double computeBetterArea(Tensor hull) {
    double intermediate = 0;
    int l = hull.length();
    for (int i = 0; i < l - 1; i++) {
      intermediate += hull.get(i).Get(0).number().doubleValue() * hull.get(i + 1).Get(1).number().doubleValue()
          - hull.get(i).Get(1).number().doubleValue() * hull.get(i + 1).Get(0).number().doubleValue();
    }
    intermediate += hull.get(l - 1).Get(0).number().doubleValue() * hull.get(0).Get(1).number().doubleValue()
        - hull.get(0).Get(1).number().doubleValue() * hull.get(l - 1).Get(0).number().doubleValue();
    return Math.abs(intermediate) / 2;
  }

  public List<Tensor> getAreas() {
    return Collections.unmodifiableList(collectionOfAreas);
  }

  public double getTotalArea() {
    return totalArea;
  }
}
