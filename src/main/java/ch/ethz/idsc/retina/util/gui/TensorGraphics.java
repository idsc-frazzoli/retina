// code by jph
package ch.ethz.idsc.retina.util.gui;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.function.Function;

import ch.ethz.idsc.tensor.Tensor;

public enum TensorGraphics {
  ;
  public static Path2D polygonToPath(Tensor tensor, Function<Tensor, Point2D> function) {
    Path2D path2D = new Path2D.Double();
    {
      Point2D point2D = function.apply(tensor.get(0));
      path2D.moveTo(point2D.getX(), point2D.getY());
    }
    tensor.stream().skip(1).forEach(dir -> {
      Point2D point2D = function.apply(dir);
      path2D.lineTo(point2D.getX(), point2D.getY());
    });
    return path2D;
  }
}
