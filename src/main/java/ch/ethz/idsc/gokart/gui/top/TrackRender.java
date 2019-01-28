// code by mh, jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.util.Objects;

import ch.ethz.idsc.gokart.core.map.TrackInterface;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.EmptyRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.tensor.Tensor;

public class TrackRender implements RenderInterface {
  private static final int RESOLUTION = 100;
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
      lineMiddle = trackInterface.getLineMiddle(RESOLUTION);
      lineLeft = trackInterface.getLineLeft(RESOLUTION);
      lineRight = trackInterface.getLineRight(RESOLUTION);
      closed = trackInterface.isClosed();
    }

    @Override // from RenderInterface
    public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
      // middle line
      float dash1[] = { 10.0f };
      BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);
      graphics.setColor(Color.RED);
      Stroke defaultStroke = graphics.getStroke();
      graphics.setStroke(dashed);
      Path2D path2d = geometricLayer.toPath2D(lineMiddle);
      if (closed)
        path2d.closePath();
      graphics.draw(path2d);
      // left line
      // graphics.setStroke(s);
      graphics.setStroke(defaultStroke);
      graphics.setColor(Color.RED);
      path2d = geometricLayer.toPath2D(lineLeft);
      if (closed)
        path2d.closePath();
      graphics.draw(path2d);
      // right line
      path2d = geometricLayer.toPath2D(lineRight);
      if (closed)
        path2d.closePath();
      graphics.draw(path2d);
    }
  }
}
