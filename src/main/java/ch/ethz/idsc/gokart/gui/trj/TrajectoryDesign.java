// code by jph
package ch.ethz.idsc.gokart.gui.trj;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Arrays;
import java.util.Objects;

import javax.swing.JToggleButton;
import javax.swing.WindowConstants;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.EmptyRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.sophus.app.api.Clothoid1Display;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.sophus.app.curve.CurvatureDemo;
import ch.ethz.idsc.sophus.app.misc.CurveCurvatureRender;
import ch.ethz.idsc.sophus.app.util.LazyMouse;
import ch.ethz.idsc.sophus.app.util.LazyMouseListener;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.LaneRiesenfeldCurveSubdivision;
import ch.ethz.idsc.sophus.lie.se2.Se2GroupElement;
import ch.ethz.idsc.sophus.lie.so2.So2;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.io.Get;
import ch.ethz.idsc.tensor.io.Put;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.sca.N;

public class TrajectoryDesign extends CurvatureDemo {
  private static final Scalar COMB_SCALE = Quantity.of(-1.0, "m^2");
  private static final Tensor OFS_L = Tensors.fromString("{0, +1[m], 0}").unmodifiable();
  private static final Tensor OFS_R = Tensors.fromString("{0, -1[m], 0}").unmodifiable();
  private static final PathRender PATH_SIDE_L = new PathRender(new Color(255, 128, 128, 192), 1);
  private static final PathRender PATH_SIDE_R = new PathRender(new Color(128, 192, 128, 192), 1);
  // ---
  private final SpinnerLabel<Integer> spinnerLabelDegree = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerLabelLevels = new SpinnerLabel<>();
  public final JToggleButton jToggleButtonRepos = new JToggleButton("repos.");
  private final SpinnerLabel<RenderPlugins> spinnerLabelPlugins = new SpinnerLabel<>();
  private RenderInterface renderInterface = EmptyRender.INSTANCE;
  private RenderPluginParameters renderPluginParameters = null;
  private final LazyMouseListener lazyMouseListener = new LazyMouseListener() {
    @Override
    public void lazyClicked(MouseEvent mouseEvent) {
      if (!jToggleButtonRepos.isSelected() && Objects.nonNull(renderPluginParameters))
        renderInterface = spinnerLabelPlugins.getValue().renderInterface(renderPluginParameters);
    }
  };

  public TrajectoryDesign() {
    super(Arrays.asList(Clothoid1Display.INSTANCE));
    jToggleCurvature.setSelected(false);
    {
      jToggleButtonRepos.setToolTipText("position control points with the mouse");
      jToggleButtonRepos.setSelected(isPositioningEnabled());
      jToggleButtonRepos.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
          boolean enabled = jToggleButtonRepos.isSelected();
          setPositioningEnabled(enabled);
          spinnerLabelPlugins.setEnabled(!enabled);
        }
      });
    }
    timerFrame.jToolBar.add(jToggleButtonRepos);
    timerFrame.jToolBar.addSeparator();
    {
      spinnerLabelDegree.setArray(1, 2, 3, 4, 5);
      spinnerLabelDegree.setValue(1);
      spinnerLabelDegree.addToComponentReduced(timerFrame.jToolBar, new Dimension(30, 28), "degree");
    }
    {
      spinnerLabelLevels.setArray(3, 4, 5);
      spinnerLabelLevels.setValue(4);
      spinnerLabelLevels.addToComponentReduced(timerFrame.jToolBar, new Dimension(30, 28), "levels");
    }
    {
      spinnerLabelPlugins.setArray(RenderPlugins.values());
      spinnerLabelPlugins.setValue(RenderPlugins.CLOTHOID_PURSUIT);
      spinnerLabelPlugins.addToComponentReduced(timerFrame.jToolBar, new Dimension(170, 28), "plugin");
      spinnerLabelPlugins.setEnabled(!jToggleButtonRepos.isSelected());
    }
    LazyMouse lazyMouse = new LazyMouse(lazyMouseListener);
    timerFrame.geometricComponent.jComponent.addMouseListener(lazyMouse);
    timerFrame.geometricComponent.jComponent.addMouseMotionListener(lazyMouse);
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
  }

  /** @param xya
   * @return {x[m], y[m], angle in the interval [-pi, pi)} */
  static Tensor se2CtoSe2WithUnits(Tensor xya) {
    xya = PoseHelper.attachUnits(xya.map(N.DOUBLE));
    xya.set(So2.MOD, 2);
    return xya;
  }

  /** @return control points of the form {x[m], y[m], heading} */
  public Tensor getControlPointsPose() {
    return Tensor.of(getControlPointsSe2().stream() //
        .map(TrajectoryDesign::se2CtoSe2WithUnits));
  }

  @Override
  public Tensor getControlPointShape() {
    return geodesicDisplay().shape().multiply(RealScalar.of(2));
  }

  /** @return refined curve */
  public Tensor getRefinedCurve() {
    Tensor control = getControlPointsPose();
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
    // ---
    renderPluginParameters = new RenderPluginParameters( //
        refined, //
        PoseHelper.attachUnits(geometricLayer.getMouseSe2State()));
    renderPluginParameters.laneBoundaryL = Tensor.of(refined.stream() //
        .map(Se2GroupElement::new) //
        .map(se2GroupElement -> se2GroupElement.combine(OFS_L)));
    renderPluginParameters.laneBoundaryR = Tensor.of(refined.stream() //
        .map(Se2GroupElement::new) //
        .map(se2GroupElement -> se2GroupElement.combine(OFS_R)));
    // ---
    PATH_SIDE_L.setCurve(renderPluginParameters.laneBoundaryL, true).render(geometricLayer, graphics);
    PATH_SIDE_R.setCurve(renderPluginParameters.laneBoundaryR, true).render(geometricLayer, graphics);
    // ---
    WaypointsRenderPlugin.INSTANCE.renderInterface(renderPluginParameters).render(geometricLayer, graphics);
    // ---
    renderInterface.render(geometricLayer, graphics);
    return refined;
  }
}
