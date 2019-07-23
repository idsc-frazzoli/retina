// code by jph
package ch.ethz.idsc.gokart.core.track;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.lie.se2.Se2Utils;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.qty.Quantity;

public class BSplineTrackDemo extends ControlPointsDemo {
  private final JToggleButton jToggleView = new JToggleButton("view");
  private final JToggleButton jToggleClosed = new JToggleButton("closed");

  public BSplineTrackDemo() {
    super(true, GeodesicDisplays.R2_ONLY);
    timerFrame.jToolBar.add(jToggleView);
    timerFrame.jToolBar.add(jToggleClosed);
    jToggleView.addActionListener(e -> setPositioningEnabled(!jToggleView.isSelected()));
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    renderControlPoints(geometricLayer, graphics);
    Tensor controlPoints = getGeodesicControlPoints();
    if (1 < controlPoints.length()) {
      Tensor points_xyr = Tensor.of(controlPoints.stream().map(row -> row.append(RealScalar.of(1))));
      points_xyr = points_xyr.map(s -> Quantity.of(s, SI.METER));
      BSplineTrack bSplineTrack = jToggleClosed.isSelected() //
          ? new CyclicBSplineTrack(points_xyr)
          : new StringBSplineTrack(points_xyr);
      RenderInterface renderInterface2 = new TrackRender().setTrack(bSplineTrack);
      renderInterface2.render(geometricLayer, graphics);
      if (jToggleView.isSelected()) {
        Tensor position = geometricLayer.getMouseSe2State().extract(0, 2).map(s -> Quantity.of(s, SI.METER));
        Tensor nearestPosition = bSplineTrack.getNearestPosition(position);
        geometricLayer.pushMatrix(Se2Utils.toSE2Translation(nearestPosition.map(Magnitude.METER)));
        boolean inTrack = bSplineTrack.isInTrack(position);
        graphics.setColor(inTrack ? Color.GREEN : Color.RED);
        graphics.setStroke(new BasicStroke(2f));
        graphics.draw(geometricLayer.toPath2D(CirclePoints.of(10).multiply(RealScalar.of(.3)), true));
        geometricLayer.popMatrix();
      }
    }
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new BSplineTrackDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 600, 600);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
