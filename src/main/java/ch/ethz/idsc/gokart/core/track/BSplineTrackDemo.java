// code by jph
package ch.ethz.idsc.gokart.core.track;

import java.awt.Graphics2D;

import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;

public class BSplineTrackDemo extends ControlPointsDemo {
  private final JToggleButton jToggleButton = new JToggleButton("closed");

  public BSplineTrackDemo() {
    super(true, GeodesicDisplays.R2_ONLY);
    timerFrame.jToolBar.add(jToggleButton);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    renderControlPoints(geometricLayer, graphics);
    Tensor controlPoints = getGeodesicControlPoints();
    if (1 < controlPoints.length()) {
      Tensor points_xyr = Tensor.of(controlPoints.stream().map(row -> row.append(RealScalar.of(1))));
      points_xyr = points_xyr.map(s -> Quantity.of(s, SI.METER));
      BSplineTrack bSplineTrack = new BSplineTrack(points_xyr, jToggleButton.isSelected());
      RenderInterface renderInterface2 = new TrackRender().setTrack(bSplineTrack);
      renderInterface2.render(geometricLayer, graphics);
    }
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new BSplineTrackDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 600, 600);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
