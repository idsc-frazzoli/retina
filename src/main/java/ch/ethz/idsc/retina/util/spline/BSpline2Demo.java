// code by jph
package ch.ethz.idsc.retina.util.spline;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Arrays;

import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.sophus.app.curve.CurvatureDemo;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class BSpline2Demo extends CurvatureDemo {
  private final JToggleButton jToggleButton = new JToggleButton("cyclic");
  private final SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
  private final PathRender pathRender = new PathRender(new Color(0, 0, 255, 128), 2f);

  public BSpline2Demo() {
    super(GeodesicDisplays.R2_ONLY);
    // ---
    timerFrame.jToolBar.add(jToggleButton);
    // ---
    spinnerRefine.setList(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));
    spinnerRefine.setValue(8);
    spinnerRefine.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "resolution");
  }

  @Override
  protected Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    AxesRender.INSTANCE.render(geometricLayer, graphics);
    renderControlPoints(geometricLayer, graphics);
    Tensor control = getGeodesicControlPoints();
    // ---
    boolean cyclic = jToggleButton.isSelected();
    int resolution = spinnerRefine.getValue();
    final int n = control.length();
    int support = cyclic ? n : n - 2;
    int offset = cyclic ? 0 : 1;
    if (0 < support) {
      Tensor domain = Tensors.vector(i -> RealScalar.of(i / (double) resolution), support * resolution + offset);
      Tensor matrixD0 = domain.map(BSpline2Vector.of(n, 0, cyclic));
      Tensor refined = matrixD0.dot(control);
      pathRender.setCurve(refined, cyclic).render(geometricLayer, graphics);
      {
        Tensor matrixD1 = domain.map(BSpline2Vector.of(n, 1, cyclic));
        Tensor diffs = matrixD1.dot(control);
        graphics.setStroke(new BasicStroke());
        graphics.setColor(Color.LIGHT_GRAY);
        for (int index = 0; index < diffs.length(); ++index) {
          Tensor p = refined.get(index);
          Tensor q = diffs.get(index);
          graphics.draw(geometricLayer.toLine2D(p, p.add(q)));
        }
      }
      return refined;
    }
    return Tensors.empty();
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new BSpline2Demo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 1000, 600);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
