// code by jph
package ch.ethz.idsc.gokart.core.track;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Ramp;

/* package */ class BSplineTrackDemo extends ControlPointsDemo {
  private static final Tensor CIRCLE = CirclePoints.of(27);
  // ---
  private final JToggleButton jToggleView = new JToggleButton("view");
  /* package */ final JToggleButton jToggleClosed = new JToggleButton("closed");

  public BSplineTrackDemo() {
    super(true, GeodesicDisplays.R2_ONLY);
    timerFrame.jToolBar.add(jToggleView);
    timerFrame.jToolBar.add(jToggleClosed);
    timerFrame.geometricComponent.addRenderInterfaceBackground(AxesRender.INSTANCE);
    jToggleView.addActionListener(e -> setPositioningEnabled(!jToggleView.isSelected()));
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    final Tensor points_xya = getControlPointsSe2().copy();
    points_xya.set(Ramp.FUNCTION, Tensor.ALL, 2);
    {
      int count = 0;
      graphics.setStroke(new BasicStroke(4f));
      graphics.setFont(new Font(Font.DIALOG, Font.BOLD, 15));
      for (Tensor point : points_xya) {
        geometricLayer.pushMatrix(Se2Matrix.translation(point));
        Path2D path2d = geometricLayer.toPath2D(CIRCLE.multiply(point.Get(2)));
        path2d.closePath();
        graphics.setColor(color(point));
        graphics.draw(path2d);
        if (count < 0) {
          Point2D point2d = geometricLayer.toPoint2D(Array.zeros(2));
          graphics.setColor(Color.BLACK);
          graphics.drawString("" + count, (int) point2d.getX() + 4, (int) point2d.getY() + 5);
        }
        geometricLayer.popMatrix();
        ++count;
      }
    }
    renderControlPoints(geometricLayer, graphics);
    if (1 < points_xya.length()) {
      Tensor points_xyr = points_xya.map(s -> Quantity.of(s, SI.METER));
      BSplineTrack bSplineTrack = jToggleClosed.isSelected() //
          ? new BSplineTrackCyclic(points_xyr)
          : new BSplineTrackString(points_xyr);
      RenderInterface renderInterface2 = new TrackRender().setTrack(bSplineTrack);
      renderInterface2.render(geometricLayer, graphics);
      if (jToggleView.isSelected()) {
        Tensor position = geometricLayer.getMouseSe2State().extract(0, 2).map(s -> Quantity.of(s, SI.METER));
        Tensor nearestPosition = bSplineTrack.getNearestPosition(position);
        geometricLayer.pushMatrix(Se2Matrix.translation(nearestPosition.map(Magnitude.METER)));
        boolean inTrack = bSplineTrack.isInTrack(position);
        graphics.setColor(inTrack ? Color.GREEN : Color.RED);
        graphics.setStroke(new BasicStroke(2f));
        graphics.draw(geometricLayer.toPath2D(CirclePoints.of(10).multiply(RealScalar.of(.3)), true));
        geometricLayer.popMatrix();
      }
    }
  }

  public Color color(Tensor point) {
    return new Color(255, 128, 128, 128);
  }

  public static void main(String[] args) throws Exception {
    AbstractDemo abstractDemo = new BSplineTrackDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 600, 600);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}