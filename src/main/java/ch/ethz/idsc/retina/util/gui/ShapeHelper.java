package ch.ethz.idsc.retina.util.gui;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import ch.ethz.idsc.tensor.Tensor;

public enum ShapeHelper {
  ;
  public static Shape path(Tensor hull) {
    Path2D path2d = new Path2D.Double();
    boolean init = false;
    for (Tensor point : hull) {
      Point2D point2d = new Point2D.Double( //
          point.Get(0).number().doubleValue(), //
          point.Get(1).number().doubleValue());
      if (init) {
        path2d.lineTo(point2d.getX(), point2d.getY());
      } else {
        path2d.moveTo(point2d.getX(), point2d.getY());
        init = true;
      }
    }
    return path2d;
  }
}
