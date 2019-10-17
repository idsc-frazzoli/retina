// code by mh, jph
package ch.ethz.idsc.gokart.core.track;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.util.Objects;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.EmptyRender;
import ch.ethz.idsc.owl.gui.ren.LaneRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.lane.LaneInterface;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.math.ArcTan2D;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.sophus.ply.Arrowhead;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/* package */ class TrackRender implements RenderInterface {
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
    private final LaneInterface lane;
    private final boolean closed;

    public Render(TrackInterface trackInterface) {
      lane = trackInterface.getTrackBoundaries(RESOLUTION);
      closed = trackInterface.isClosed();
    }

    @Override // from RenderInterface
    public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
      GraphicsUtil.setQualityHigh(graphics);
      float width = geometricLayer.model2pixelWidth(0.15);
      Stroke strokeDashed = //
          new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] { 10.0f }, 0.0f);
      { // middle line
        graphics.setStroke(strokeDashed);
        Tensor lineMiddle = Tensor.of(lane.midLane().stream().map(Extract2D.FUNCTION));
        Path2D path2d = geometricLayer.toPath2D(lineMiddle, closed);
        graphics.setColor(new Color(255, 0, 0, 192));
        graphics.draw(path2d);
        render(geometricLayer, graphics, lineMiddle);
      }
      {
        // side lines
        LaneRender laneRender = new LaneRender();
        laneRender.setLane(lane, closed);
        laneRender.render(geometricLayer, graphics);
      }
      GraphicsUtil.setQualityDefault(graphics);
    }

    private void render(GeometricLayer geometricLayer, Graphics2D graphics, Tensor line) {
      graphics.setColor(new Color(64, 64, 64, 192));
      for (int index = INTERVALS; index < line.length() - 1; index += INTERVALS) {
        Tensor xy = line.get(index);
        Tensor vector = line.get(index + 1).subtract(xy);
        Scalar angle = ArcTan2D.of(vector);
        geometricLayer.pushMatrix(Se2Matrix.of(xy.map(Magnitude.METER).append(angle)));
        Path2D path2d = geometricLayer.toPath2D(ARROWHEAD);
        graphics.fill(path2d);
        geometricLayer.popMatrix();
        graphics.setColor(new Color(128, 128, 128, 128));
      }
    }
  }
}
