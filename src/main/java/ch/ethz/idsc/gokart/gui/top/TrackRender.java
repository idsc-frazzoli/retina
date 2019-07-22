// code by mh, jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.util.Objects;

import ch.ethz.idsc.gokart.core.track.TrackBoundaries;
import ch.ethz.idsc.gokart.core.track.TrackInterface;
import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.EmptyRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.sophus.lie.se2.Se2Utils;
import ch.ethz.idsc.sophus.math.ArcTan2D;
import ch.ethz.idsc.sophus.ply.Arrowhead;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class TrackRender implements RenderInterface {
  private static final int RESOLUTION = 100;
  private static final Tensor ARROWHEAD = Arrowhead.of(0.75);
  private static final int INTERVALS = 10;
  // ---
  private RenderInterface renderInterface = EmptyRender.INSTANCE;

  /** @param trackInterface may be null
   * @return */
  public RenderInterface setTrack(TrackInterface trackInterface) {
    return renderInterface = Objects.isNull(trackInterface) //
        ? EmptyRender.INSTANCE
        : new Render(trackInterface);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    renderInterface.render(geometricLayer, graphics);
  }

  private class Render implements RenderInterface {
    private final Tensor lineMiddle;
    private final Tensor lineLeft;
    private final Tensor lineRight;
    private final boolean closed;

    public Render(TrackInterface trackInterface) {
      TrackBoundaries trackBoundaries = trackInterface.getTrackBoundaries(RESOLUTION);
      lineMiddle = trackBoundaries.getLineCenter();
      lineLeft = trackBoundaries.getLineLeft();
      lineRight = trackBoundaries.getLineRight();
      closed = trackInterface.isClosed();
    }

    @Override // from RenderInterface
    public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
      GraphicsUtil.setQualityHigh(graphics);
      float width = geometricLayer.model2pixelWidth(0.15);
      Stroke strokeDashed = //
          new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] { 10.0f }, 0.0f);
      Stroke strokeNormal = new BasicStroke(width);
      { // middle line
        graphics.setStroke(strokeDashed);
        Path2D path2d = geometricLayer.toPath2D(lineMiddle);
        if (closed)
          path2d.closePath();
        graphics.setColor(new Color(255, 0, 0, 192));
        graphics.draw(path2d);
        graphics.setStroke(strokeNormal);
        render(geometricLayer, graphics, lineMiddle);
      }
      { // left line
        Path2D path2d = geometricLayer.toPath2D(lineLeft);
        if (closed)
          path2d.closePath();
        graphics.setColor(new Color(255, 0, 0, 192));
        if (true) {
          graphics.draw(path2d);
          render(geometricLayer, graphics, lineLeft);
        }
      }
      { // right line
        Path2D path2d = geometricLayer.toPath2D(lineRight);
        if (closed)
          path2d.closePath();
        graphics.setColor(new Color(0, 255, 0, 192));
        if (true) {
          graphics.draw(path2d);
          render(geometricLayer, graphics, lineRight);
        }
      }
      GraphicsUtil.setQualityDefault(graphics);
    }

    private void render(GeometricLayer geometricLayer, Graphics2D graphics, Tensor line) {
      graphics.setColor(new Color(64, 64, 64, 192));
      for (int index = INTERVALS; index < line.length() - 1; index += INTERVALS) {
        Tensor xy = line.get(index);
        Tensor vector = line.get(index + 1).subtract(xy);
        Scalar angle = ArcTan2D.of(vector);
        geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(xy.map(Magnitude.METER).append(angle)));
        Path2D path2d = geometricLayer.toPath2D(ARROWHEAD);
        graphics.fill(path2d);
        geometricLayer.popMatrix();
        graphics.setColor(new Color(128, 128, 128, 128));
      }
    }
  }
}
