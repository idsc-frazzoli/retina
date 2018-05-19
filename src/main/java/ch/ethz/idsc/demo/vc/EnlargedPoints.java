// code by vc
package ch.ethz.idsc.demo.vc;

import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;

import ch.ethz.idsc.tensor.Tensor;

public class EnlargedPoints {
  public List<Area> collectionOfAreas = new ArrayList<>(); // TODO
  private int w = 2; // TODO

  public EnlargedPoints(Tensor points) {
    for (Tensor x : points) {
      addToCollection(x);
    }
  }

  public void addToCollection(Tensor point) {
    collectionOfAreas.add(new Area(new Ellipse2D.Double(point.Get(0).number().doubleValue() - w / 2, point.Get(1).number().doubleValue() - w / 2, w, w)));
  }
}
