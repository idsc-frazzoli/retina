// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Arrays;

import javax.swing.WindowConstants;

import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.sophus.app.api.ClothoidCurveDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.curve.CurvatureDemo;
import ch.ethz.idsc.sophus.app.misc.CurveCurvatureRender;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.curve.CurveSubdivision;
import ch.ethz.idsc.sophus.curve.LaneRiesenfeldCurveSubdivision;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Get;
import ch.ethz.idsc.tensor.io.Put;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Nest;

public class TrajectoryDesign extends CurvatureDemo {
  private static final Scalar COMB_SCALE = Quantity.of(-1.0, "m^2");
  private final SpinnerLabel<Integer> spinnerLabelDegree = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerLabelLevels = new SpinnerLabel<>();

  public TrajectoryDesign() {
    super(Arrays.asList(ClothoidCurveDisplay.INSTANCE));
    jToggleCurvature.setSelected(false);
    timerFrame.jToolBar.addSeparator();
    spinnerLabelDegree.setArray(1, 2, 3, 4, 5);
    spinnerLabelDegree.setValue(1);
    spinnerLabelDegree.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "degree");
    spinnerLabelLevels.setArray(3, 4, 5);
    spinnerLabelLevels.setValue(4);
    spinnerLabelLevels.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "levels");
    // ---
    final File file = AppCustomization.file(getClass(), "model2pixel.tensor");
    try {
      timerFrame.geometricComponent.setModel2Pixel(Get.of(file));
    } catch (Exception exception) {
      // ---
    }
    timerFrame.jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        try {
          Put.of(file, timerFrame.geometricComponent.getModel2Pixel());
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
    });
    timerFrame.jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    timerFrame.jFrame.setVisible(true);
  }

  /** @return control points of the form {x[m], y[m], heading} */
  public Tensor controlPoints() {
    return Tensor.of(control().stream().map(PoseHelper::attachUnits));
  }

  /** @return refined curve */
  public Tensor getRefinedCurve() {
    Tensor control = controlPoints();
    int degree = spinnerLabelDegree.getValue();
    CurveSubdivision curveSubdivision = new LaneRiesenfeldCurveSubdivision(geodesicDisplay().geodesicInterface(), degree);
    int levels = spinnerLabelLevels.getValue();
    return Nest.of(curveSubdivision::cyclic, control, levels);
  }

  @Override // from CurvatureDemo
  public Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    renderControlPoints(geometricLayer, graphics);
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor refined = getRefinedCurve();
    Tensor render = Tensor.of(refined.stream().map(geodesicDisplay::toPoint));
    CurveCurvatureRender.of(render, true, COMB_SCALE, geometricLayer, graphics);
    return refined;
  }

  public static void main(String[] args) {
    TrajectoryDesign trajectoryDesign = new TrajectoryDesign();
    trajectoryDesign.timerFrame.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }
}
