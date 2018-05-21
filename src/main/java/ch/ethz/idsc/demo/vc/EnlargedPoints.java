// code by vc
package ch.ethz.idsc.demo.vc;

import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class EnlargedPoints {
  private final List<Area> collectionOfAreas = new ArrayList<>();
  private double totalArea;

  public EnlargedPoints(Tensor points, double w) {
    totalArea = points.length() * w * w;
    for (Tensor x : points) {
      addToCollection(x, w);
    }
  }

  public EnlargedPoints(Tensor hulls) {
    for (Tensor hull : hulls) {
      Path2D path2d = new Path2D.Double();
      if (Tensors.nonEmpty(hull)) {
        path2d.moveTo(hull.get(0).Get(1).number().doubleValue(), hull.get(0).Get(1).number().doubleValue());
        for (Tensor point : hull) {
          path2d.lineTo(point.Get(0).number().doubleValue(), point.Get(1).number().doubleValue());
        }
        Area area = new Area(path2d);
        collectionOfAreas.add(area);
        totalArea = totalArea + computeArea(area);
      }
    }
  }

  public void addToCollection(Tensor point, double w) {
    collectionOfAreas.add(new Area(new Rectangle2D.Double(point.Get(0).number().doubleValue() - w / 2, point.Get(1).number().doubleValue() - w / 2, w, w)));
  }

  public double computeArea(Area area) {
    double width = area.getBounds2D().getWidth();
    double height = area.getBounds2D().getHeight();
    return height * width;
  }

  public List<Area> getAreas() {
    return Collections.unmodifiableList(collectionOfAreas);
  }

  public double getTotalArea() {
    return totalArea;
  }
}
