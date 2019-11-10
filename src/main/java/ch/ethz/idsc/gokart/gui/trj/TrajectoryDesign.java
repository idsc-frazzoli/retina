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

import ch.ethz.idsc.gokart.core.adas.HapticSteerConfig;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.EmptyRender;
import ch.ethz.idsc.owl.gui.ren.LaneRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.lane.LaneInterface;
import ch.ethz.idsc.owl.lane.StableLane;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.sophus.app.api.ClothoidDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.curve.CurvatureDemo;
import ch.ethz.idsc.sophus.app.misc.Curvature2DRender;
import ch.ethz.idsc.sophus.app.util.LazyMouse;
import ch.ethz.idsc.sophus.app.util.LazyMouseListener;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.LaneRiesenfeldCurveSubdivision;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.lie.so2.So2;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Get;
import ch.ethz.idsc.tensor.io.Put;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.sca.N;

public class TrajectoryDesign extends CurvatureDemo {
  private static final Scalar COMB_SCALE = Quantity.of(-1.0, "m^2");
  // ---
  private final SpinnerLabel<Integer> spinnerLabelDegree = new SpinnerLabel<>();
  private final SpinnerLabel<Integer> spinnerLabelLevels = new SpinnerLabel<>();
  private final JToggleButton jToggleButtonWaypoints = new JToggleButton("wayp.");
  public final JToggleButton jToggleButtonRepos = new JToggleButton("repos.");
  private final SpinnerLabel<RenderPlugins> spinnerLabelPlugins = new SpinnerLabel<>();
  private RenderInterface renderInterface = EmptyRender.INSTANCE;
  private RenderPluginParameters renderPluginParameters = null;
  private final LaneRender laneRender = new LaneRender();
  private final LazyMouseListener lazyMouseListener = new LazyMouseListener() {
    @Override
    public void lazyClicked(MouseEvent mouseEvent) {
      if (!jToggleButtonRepos.isSelected() && Objects.nonNull(renderPluginParameters))
        renderInterface = spinnerLabelPlugins.getValue().renderInterface(renderPluginParameters);
    }
  };

  public TrajectoryDesign() {
    super(Arrays.asList(ClothoidDisplay.INSTANCE));
    jToggleCurvature.setSelected(false);
    timerFrame.jToolBar.add(jToggleButtonWaypoints);
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

  @Override // from ControlPointsDemo
  protected Tensor getControlPointShape() {
    return geodesicDisplay().shape().multiply(RealScalar.of(2));
  }

  /** @return refined curve */
  public Tensor getRefinedCurve() {
    Tensor control = getControlPointsPose();
    int degree = spinnerLabelDegree.getValue();
    CurveSubdivision curveSubdivision = LaneRiesenfeldCurveSubdivision.of(geodesicDisplay().geodesicInterface(), degree);
    int levels = spinnerLabelLevels.getValue();
    return Nest.of(curveSubdivision::cyclic, control, levels);
  }

  private final FootprintRender footprintRender = new FootprintRender(new Color(0, 128, 128, 64));

  @Override // from CurvatureDemo
  public Tensor protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    renderControlPoints(geometricLayer, graphics);
    if (!jToggleButtonRepos.isSelected()) {
      geometricLayer.pushMatrix(Se2Matrix.of(geometricLayer.getMouseSe2State()));
      footprintRender.render(geometricLayer, graphics);
      geometricLayer.popMatrix();
    }
    GeodesicDisplay geodesicDisplay = geodesicDisplay();
    Tensor refined = getRefinedCurve();
    Tensor render = Tensor.of(refined.stream().map(geodesicDisplay::toPoint));
    Curvature2DRender.of(render, true, COMB_SCALE, geometricLayer, graphics);
    // ---
    renderPluginParameters = new RenderPluginParameters( //
        refined, //
        PoseHelper.attachUnits(geometricLayer.getMouseSe2State()));
    LaneInterface laneInterface = //
        StableLane.of(getControlPointsPose(), refined, HapticSteerConfig.GLOBAL.halfWidth);
    renderPluginParameters.laneBoundaryL = laneInterface.leftBoundary();
    renderPluginParameters.laneBoundaryR = laneInterface.rightBoundary();
    // ---
    laneRender.setLane(laneInterface, true);
    laneRender.render(geometricLayer, graphics);
    // ---
    if (jToggleButtonWaypoints.isSelected())
      WaypointsRenderPlugin.INSTANCE.renderInterface(renderPluginParameters).render(geometricLayer, graphics);
    // ---
    renderInterface.render(geometricLayer, graphics);
    return refined;
  }
}
