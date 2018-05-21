// code by vc
package ch.ethz.idsc.demo.vc;

import java.awt.Polygon;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Primitives;

public class EnlargedPoints {
  public List<Area> collectionOfAreas = new ArrayList<>(); // TODO
  double totalArea = 0;

  public EnlargedPoints(Tensor points, double w) {
    totalArea = points.length() * w * w;
    for (Tensor x : points) {
      addToCollection(x, w);
    }
  }

  public EnlargedPoints(Tensor hulls) {
    for (Tensor hull : hulls) {
      double[][] data = Primitives.toDoubleArray2D(hull);
      int[] xpoints = getColumn(data, 0);
      int[] ypoints = getColumn(data, 1);
      collectionOfAreas.add(new Area(new Polygon(xpoints, ypoints, xpoints.length)));
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

  int[] getColumn(double[][] matrix, int column) {
    return IntStream.range(0, matrix.length).map(i -> (int) matrix[i][column]).toArray();
  }
}
