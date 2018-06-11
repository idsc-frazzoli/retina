package ch.ethz.idsc.demo.vc;

import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.planar.PolygonArea;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.IdentityMatrix;

public class EnlargedPoints {
  private static final GeometricLayer IDENTITY_LAYER = GeometricLayer.of(IdentityMatrix.of(3));
  private Area area = new Area();
  private double totalArea;

  public EnlargedPoints(Tensor points, double w) {
    for (Tensor x : points)
      addToCollection(x, w);
    totalArea = areaCalculator(area);
  }

  public EnlargedPoints(Tensor hulls) {
    for (Tensor hull : hulls) {
      if (Tensors.nonEmpty(hull)) {
        Path2D path2d = IDENTITY_LAYER.toPath2D(hull);
        path2d.closePath();
        Area hl = new Area(path2d);
        area.add(hl);
        totalArea = totalArea + computeBetterArea(hull);
      }
    }
  }

  private void addToCollection(Tensor point, double w) {
    area.add(new Area(new Rectangle2D.Double( //
        point.Get(0).number().doubleValue() - w / 2, //
        point.Get(1).number().doubleValue() + w / 2, w, w)));
  }

  private double computeBetterArea(Tensor polygon) {
    return PolygonArea.FUNCTION.apply(polygon).abs().number().doubleValue();
  }

  public static double areaCalculator(Area area) {
    int count = 0;
    Rectangle2D bounds2d = area.getBounds2D();
    double x = bounds2d.getX();
    double y = bounds2d.getY();
    double width = bounds2d.getWidth();
    double height = bounds2d.getHeight();
    for (int i = 0; i < 300; i++) {
      for (int j = 0; j < 300; j++) {
        Point2D point = new Point2D.Double(x + i * width / 300, y + j * height / 300);
        if (area.contains(point))
          ++count;
      }
    }
    return height * width * count / 90000;
  }

  public double getTotalArea() {
    return totalArea;
  }

  public Area getArea() {
    return (Area) area.clone();
  }
}