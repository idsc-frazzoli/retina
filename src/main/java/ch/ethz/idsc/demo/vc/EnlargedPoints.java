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
        // new Path2D.Double();
        // path2d.moveTo(hull.get(0).Get(1).number().doubleValue(), hull.get(0).Get(1).number().doubleValue());
        // for (Tensor point : hull) {
        // path2d.lineTo(point.Get(0).number().doubleValue(), point.Get(1).number().doubleValue());
        // }
        Area area = new Area(path2d);
        collectionOfAreas.add(area);
        // TODO would it be beneficial to compute the exact volume of the hull (instead of that of the bounding box)?
        // https://www.mathopenref.com/coordpolygonarea.html
        totalArea = totalArea + computeArea(area);
      }
    }
  }

  // private since function does not update totalArea
  private void addToCollection(Tensor point, double w) {
    collectionOfAreas.add(new Area(new Rectangle2D.Double( //
        point.Get(0).number().doubleValue() - w / 2, //
        point.Get(1).number().doubleValue() - w / 2, w, w)));
  }

  public double computeArea(Area area) {
    Rectangle2D rectangle2d = area.getBounds2D();
    return rectangle2d.getWidth() * rectangle2d.getHeight();
  }

  public List<Area> getAreas() {
    return Collections.unmodifiableList(collectionOfAreas);
  }

  public double getTotalArea() {
    return totalArea;
  }
}
