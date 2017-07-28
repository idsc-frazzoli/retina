// code by jph
package ch.ethz.idsc.retina.util.gui;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.function.Function;

import ch.ethz.idsc.tensor.Tensor;

public enum TensorGraphics {
  ;
  public static Path2D polygonToPath(Tensor tensor, Function<Tensor, Point2D> function) {
    Path2D path2d = new Path2D.Double();
    {
      Point2D point2d = function.apply(tensor.get(0));
      path2d.moveTo(point2d.getX(), point2d.getY());
    }
    tensor.flatten(0).skip(1).forEach(dir -> {
      final Point2D point = function.apply(dir);
      path2d.lineTo(point.getX(), point.getY());
    });
    return path2d;
  }
}
