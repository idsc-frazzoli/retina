package ch.ethz.idsc.gokart.gui.top;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.ethz.idsc.gokart.core.mpc.Track;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;

public class TrackRender implements RenderInterface {
  // private final Track track;
  private final Tensor leftBoundary;
  private final Tensor rightBoundary;
  private final Tensor middleLine;
  private static int resolution = 100;

  public TrackRender(Track track) {
    // this.track = track;
    this.leftBoundary = track.getLeftLine(resolution);
    this.rightBoundary = track.getRightLine(resolution);
    this.middleLine = track.getMiddleLine(resolution);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    // middle line
    float dash1[] = { 10.0f };
    BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);
    graphics.setColor(Color.YELLOW);
    // graphics.setStroke(dashed);
    Path2D path2d = geometricLayer.toPath2D(middleLine);
    path2d.closePath();
    graphics.draw(path2d);
    // left line
    // graphics.setStroke(s);
    path2d = geometricLayer.toPath2D(leftBoundary);
    path2d.closePath();
    graphics.draw(path2d);
    // right line
    path2d = geometricLayer.toPath2D(rightBoundary);
    path2d.closePath();
    graphics.draw(path2d);
  }
}
