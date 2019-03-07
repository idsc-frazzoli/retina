// code by mh
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.ethz.idsc.gokart.core.map.BSplineTrack;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;

public class TrackControlPointsRender implements RenderInterface {
  private final Tensor controlPoints;

  public TrackControlPointsRender(BSplineTrack track) {
    this.controlPoints = track.getControlPoints();
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    // draw
    graphics.setColor(Color.GREEN);
    Path2D path2d = geometricLayer.toPath2D(controlPoints);
    graphics.draw(path2d);
  }
}
