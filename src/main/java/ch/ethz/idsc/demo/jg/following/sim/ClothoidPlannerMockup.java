// code by gjoel
package ch.ethz.idsc.demo.jg.following.sim;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.Optional;
import java.util.stream.IntStream;

import javax.swing.JToggleButton;
import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.core.pure.ClothoidPlan;
import ch.ethz.idsc.gokart.core.pure.ClothoidPursuitConfig;
import ch.ethz.idsc.gokart.core.pure.CurveClothoidPursuitPlanner;
import ch.ethz.idsc.gokart.gui.top.TrajectoryDesignModule;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.pursuit.ClothoidTerminalRatios;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.sca.Sign;

/* package */ class ClothoidPlannerMockup extends TrajectoryDesignModule {
  private static final int REFINEMENT = 3;
  // ---
  private final CurveClothoidPursuitPlanner planner = new CurveClothoidPursuitPlanner();
  protected final JToggleButton jToggleButton = new JToggleButton("cloth");
  private final SpinnerLabel<Scalar> spinnerLabelSpeed = new SpinnerLabel<>();
  // ---
  protected Optional<ClothoidPlan> optional = Optional.empty();
  private Tensor mouseSe2 = Array.zeros(3);
  private final RenderInterface renderInterface = new RenderInterface() {
    @Override
    public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
      if (!trajectoryDesign.jToggleButton.isSelected()) {
        mouseSe2 = geometricLayer.getMouseSe2State();
        if (optional.isPresent()) {
          Tensor refined = Nest.of(ClothoidTerminalRatios.CURVE_SUBDIVISION::string, optional.get().curve(), REFINEMENT);
          Path2D path2d = geometricLayer.toPath2D(refined);
          graphics.setColor(Color.MAGENTA);
          graphics.draw(path2d);
        }
      }
    }
  };

  @Override // from AbstractModule
  protected void first() {
    super.first();
    {
      trajectoryDesign.jToggleButton.addActionListener(l -> {
        if (trajectoryDesign.jToggleButton.isSelected()) {
          trajectoryDesign.jToggleButton.setText("repos.");
          trajectoryDesign.jToggleButton.setToolTipText("position control points with the mouse");
        } else {
          trajectoryDesign.jToggleButton.setText("cloth");
          trajectoryDesign.jToggleButton.setToolTipText("clothoid pursuit planner");
        }
      });
    }
    {
      trajectoryDesign.timerFrame.jToolBar.addSeparator();
      spinnerLabelSpeed.setStream(IntStream.range(-5, 11).mapToObj(i -> Quantity.of(i, SI.VELOCITY)));
      spinnerLabelSpeed.setValue(Quantity.of(5, SI.VELOCITY));
      spinnerLabelSpeed.addToComponentReduced(trajectoryDesign.timerFrame.jToolBar, new Dimension(50, 28), "speed");
    }
    {
      trajectoryDesign.timerFrame.geometricComponent.jComponent.addMouseListener(new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent mouseEvent) {
          if (!trajectoryDesign.jToggleButton.isSelected() && mouseEvent.getButton() == 1) {
            Timing timing = Timing.started();
            optional = planner.getPlan(PoseHelper.attachUnits(mouseSe2), //
                spinnerLabelSpeed.getValue(), //
                trajectoryDesign.getRefinedCurve(), //
                Sign.isPositiveOrZero(spinnerLabelSpeed.getValue()), //
                ClothoidPursuitConfig.ratioLimits());
            timing.stop();
            Scalar duration = Quantity.of(timing.seconds(), SI.SECOND);
            String msg = (optional.isPresent() ? "NEW" : "NO") + " clothoid plan found in " + duration;
            if (optional.isPresent() && Scalars.lessEquals(duration, ClothoidPursuitConfig.GLOBAL.updatePeriod))
              System.out.println(msg);
            else
              System.err.println(msg);
          }
        }
      });
    }
    trajectoryDesign.timerFrame.geometricComponent.addRenderInterface(renderInterface);
  }

  public static void main(String[] args) {
    ClothoidPlannerMockup clothoidPlannerMockup = new ClothoidPlannerMockup();
    clothoidPlannerMockup.first();
    clothoidPlannerMockup.trajectoryDesign.timerFrame.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }
}
