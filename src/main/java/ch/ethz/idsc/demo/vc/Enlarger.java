// code by vc
package ch.ethz.idsc.demo.vc;

import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class Enlarger {
  private final List<Tensor> collectionOfAreas = new ArrayList<>();
  private double totalArea;

  public Enlarger(Tensor points, double w) {
    for (Tensor x : points) {
      totalArea = totalArea + addToCollection(x, w);
    }
  }

  public Enlarger(Tensor hulls) {
    for (Tensor hull : hulls) {
      if (Tensors.nonEmpty(hull)) {
        collectionOfAreas.add(hull);
        totalArea += computeBetterArea(hull);
      }
    }
  }

  private double addToCollection(Tensor point, double w) {
    double xCenter = point.Get(0).number().doubleValue();
    double yCenter = point.Get(1).number().doubleValue();
    Tensor s = Tensors.empty();
    s.append(Tensors.vectorDouble(xCenter - (w / 2), yCenter - (w / 2)));
    s.append(Tensors.vectorDouble(xCenter - (w / 2), yCenter + (w / 2)));
    s.append(Tensors.vectorDouble(xCenter + (w / 2), yCenter + (w / 2)));
    s.append(Tensors.vectorDouble(xCenter + (w / 2), yCenter - (w / 2)));
    collectionOfAreas.add(s);
    return computeBetterArea(s);
  }

  public double computeArea(Area area) {
    Rectangle2D rectangle2d = area.getBounds2D();
    return rectangle2d.getWidth() * rectangle2d.getHeight();
  }

  public double computeBetterArea(Tensor hull) { // inspired of https://www.mathopenref.com/coordpolygonarea.html
    int l = hull.length();
    double intermediate = hull.get(l - 1).Get(0).number().doubleValue() * hull.get(0).Get(1).number().doubleValue()
        - hull.get(0).Get(0).number().doubleValue() * hull.get(l - 1).Get(1).number().doubleValue();
    for (int i = 0; i < (l - 1); i++) {
      intermediate += hull.get(i).Get(0).number().doubleValue() * hull.get(i + 1).Get(1).number().doubleValue()
          - hull.get(i).Get(1).number().doubleValue() * hull.get(i + 1).Get(0).number().doubleValue();
    }
    return Math.abs(intermediate) / 2;
  }

  public List<Tensor> getAreas() {
    return Collections.unmodifiableList(collectionOfAreas);
  }

  public double getTotalArea() {
    return totalArea;
  }
}
