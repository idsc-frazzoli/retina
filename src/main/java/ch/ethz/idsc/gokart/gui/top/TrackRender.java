// code by mh
package ch.ethz.idsc.gokart.gui.top;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Path2D;

import ch.ethz.idsc.gokart.core.mpc.TrackInterface;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;

public class TrackRender implements RenderInterface {
  private static final int RESOLUTION = 100;
  // ---
  private final Tensor leftBoundary;
  private final Tensor rightBoundary;
  private final Tensor middleLine;
  private final boolean closed;

  public TrackRender(TrackInterface track) {
    this.leftBoundary = track.getLeftLine(RESOLUTION);
    this.rightBoundary = track.getRightLine(RESOLUTION);
    this.middleLine = track.getMiddleLine(RESOLUTION);
    this.closed = track.isClosed();
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    // middle line
    float dash1[] = { 10.0f };
    Stroke defaultStroke;
    BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);
    graphics.setColor(Color.RED);
    defaultStroke = graphics.getStroke();
    graphics.setStroke(dashed);
    Path2D path2d = geometricLayer.toPath2D(middleLine);
    if (closed)
      path2d.closePath();
    graphics.draw(path2d);
    // left line
    // graphics.setStroke(s);
    graphics.setStroke(defaultStroke);
    graphics.setColor(Color.WHITE);
    path2d = geometricLayer.toPath2D(leftBoundary);
    if (closed)
      path2d.closePath();
    graphics.draw(path2d);
    // right line
    path2d = geometricLayer.toPath2D(rightBoundary);
    if (closed)
      path2d.closePath();
    graphics.draw(path2d);
  }

  public void renderHR(GeometricLayer geometricLayer, Graphics2D graphics) {
    // middle line
    float width = geometricLayer.getMatrix().get(0).Get(0).number().floatValue()/7.5f;
    
    float dash1[] = { width*10.0f };
    Stroke defaultStroke;
    BasicStroke dashed = new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);
    graphics.setColor(Color.RED);
    defaultStroke = graphics.getStroke();
    graphics.setStroke(dashed);
    Path2D path2d = geometricLayer.toPath2D(middleLine);
    if (closed)
      path2d.closePath();
    graphics.draw(path2d);
    // left line
    // graphics.setStroke(s);
    BasicStroke thick = new BasicStroke(width);
    graphics.setStroke(thick);
    graphics.setColor(Color.YELLOW);
    path2d = geometricLayer.toPath2D(leftBoundary);
    if (closed)
      path2d.closePath();
    graphics.draw(path2d);
    // right line
    path2d = geometricLayer.toPath2D(rightBoundary);
    if (closed)
      path2d.closePath();
    graphics.draw(path2d);
    graphics.setColor(Color.WHITE);
    graphics.setStroke(defaultStroke);
  }
}
