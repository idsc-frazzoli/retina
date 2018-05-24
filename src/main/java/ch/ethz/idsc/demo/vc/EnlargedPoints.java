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

public class EnlargedPoints {
  private static final GeometricLayer IDENTITY_LAYER = GeometricLayer.of(IdentityMatrix.of(3));
  // ---
  private final List<Area> collectionOfAreas = new ArrayList<>();
  private double totalArea;

  public EnlargedPoints(Tensor points, double w) {
    totalArea = points.length() * w * w;
    for (Tensor x : points)
      addToCollection(x, w);
  }

  public EnlargedPoints(Tensor hulls) {
    for (Tensor hull : hulls) {
      if (Tensors.nonEmpty(hull)) {
        Path2D path2d = IDENTITY_LAYER.toPath2D(hull);
        path2d.closePath();
        Area area = new Area(path2d);
        collectionOfAreas.add(area);
        totalArea = totalArea + computeBetterArea(hull);
      }
    }
  }

  // private since function does not update totalArea
  private void addToCollection(Tensor point, double w) {
    collectionOfAreas.add(new Area(new Rectangle2D.Double( //
        point.Get(0).number().doubleValue() - w / 2, //
        point.Get(1).number().doubleValue() - w / 2, w, w)));
  }

  public double computeArea(Area area) { // TODO: compute the exact surface of an area
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

  public List<Area> getAreas() {
    return Collections.unmodifiableList(collectionOfAreas);
  }

  public double getTotalArea() {
    return totalArea;
  }
}
