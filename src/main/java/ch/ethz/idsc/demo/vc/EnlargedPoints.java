// code by vc
package ch.ethz.idsc.demo.vc;

import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;

/* package */ class EnlargedPoints {
  private static final GeometricLayer IDENTITY_LAYER = GeometricLayer.of(IdentityMatrix.of(3));

  public static Area toArea(Tensor polygon) {
    Path2D path2d = IDENTITY_LAYER.toPath2D(polygon);
    path2d.closePath();
    return new Area(path2d);
  }

  // ---
  private final Area area = new Area();
  private double totalArea;

  public EnlargedPoints(Tensor points, double w) {
    for (Tensor x : points)
      addToCollection(x, w);
    totalArea = AreaMeasure.of(area);
  }

  public EnlargedPoints(Tensor hulls) {
    for (Tensor hull : hulls)
      if (Tensors.nonEmpty(hull)) {
        area.add(toArea(hull));
        totalArea += StaticHelper.computeBetterArea(hull);
      }
  }

  private void addToCollection(Tensor point, double w) {
    area.add(new Area(new Rectangle2D.Double( //
        point.Get(0).number().doubleValue() - w / 2, //
        point.Get(1).number().doubleValue() + w / 2, w, w)));
  }

  public double getTotalArea() {
    return totalArea;
  }

  public Area getArea() {
    return new Area(area);
  }
}