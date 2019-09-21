// code by jph
package ch.ethz.idsc.gokart.core.track;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import javax.swing.JButton;
import javax.swing.JToggleButton;

import ch.ethz.idsc.gokart.core.slam.PredefinedMap;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.ren.GridRender;
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
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Ramp;
import ch.ethz.idsc.tensor.sca.Round;

/** the GUI allows to layout control points that define the quadratic-BSpline center line
 * and lane radius.
 * 
 * the radius is modified by selecting the control point and rotating the mouse wheel. */
/* package */ class BSplineTrackDemo extends ControlPointsDemo {
  private static final Tensor CIRCLE = CirclePoints.of(27);
  private static final GridRender GRID_RENDER = new GridRender(Subdivide.of(0, 80, 8));
  // ---
  private final JToggleButton jToggleView = new JToggleButton("view");
  /* package */ final JToggleButton jToggleOpen = new JToggleButton("open");
  private final JButton jTogglePrint = new JButton("print");

  public BSplineTrackDemo() {
    super(true, GeodesicDisplays.R2_ONLY);
    // ---
    jToggleView.setToolTipText("");
    timerFrame.jToolBar.add(jToggleView);
    // ---
    jToggleOpen.setToolTipText("select track open or closed");
    timerFrame.jToolBar.add(jToggleOpen);
    // ---
    timerFrame.geometricComponent.addRenderInterfaceBackground(AxesRender.INSTANCE);
    jToggleView.addActionListener(e -> setPositioningEnabled(!jToggleView.isSelected()));
    timerFrame.geometricComponent.setModel2Pixel(PredefinedMap.DUBILAB_LOCALIZATION_20190708.getModel2Pixel());
    // ---
    timerFrame.jToolBar.addSeparator();
    jTogglePrint.addActionListener(e -> {
      System.out.println(Pretty.of(getXYR().map(Round._3)));
    });
    timerFrame.jToolBar.add(jTogglePrint);
  }

  private Tensor getXYR() {
    Tensor points_xya = getControlPointsSe2().copy();
    points_xya.set(Ramp.FUNCTION, Tensor.ALL, 2);
    return points_xya;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    GRID_RENDER.render(geometricLayer, graphics);
    // ---
    final Tensor points_xya = getXYR();
    {
      int count = 0;
      graphics.setStroke(new BasicStroke(3f));
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
          graphics.drawString("" + count, //
              (int) point2d.getX() + 4, //
              (int) point2d.getY() + 5);
        }
        geometricLayer.popMatrix();
        ++count;
      }
    }
    renderControlPoints(geometricLayer, graphics);
    if (1 < points_xya.length()) {
      Tensor points_xyr = points_xya.map(s -> Quantity.of(s, SI.METER));
      BSplineTrack bSplineTrack = jToggleOpen.isSelected() //
          ? new BSplineTrackString(points_xyr)
          : new BSplineTrackCyclic(points_xyr);
      RenderInterface renderInterface = new TrackRender().setTrack(bSplineTrack);
      renderInterface.render(geometricLayer, graphics);
      if (jToggleView.isSelected()) {
        Tensor position = geometricLayer.getMouseSe2State().extract(0, 2).map(s -> Quantity.of(s, SI.METER));
        Tensor nearestPosition = bSplineTrack.getNearestPosition(position);
        geometricLayer.pushMatrix(Se2Matrix.translation(nearestPosition.map(Magnitude.METER)));
        boolean inTrack = bSplineTrack.isInTrack(position);
        graphics.setColor(inTrack ? Color.GREEN : Color.RED);
        graphics.setStroke(new BasicStroke(2f));
        graphics.draw(geometricLayer.toPath2D(CirclePoints.of(10), true));
        geometricLayer.popMatrix();
      }
    }
  }

  @Override
  protected Tensor getControlPointShape() {
    return super.getControlPointShape().multiply(RealScalar.of(4));
  }

  public Color color(Tensor point) {
    return new Color(255, 128, 128, 128);
  }

  public static void main(String[] args) throws Exception {
    AbstractDemo abstractDemo = new BSplineTrackDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 800);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
