// code by gjoel
package ch.ethz.idsc.demo.jg.following.sim;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.Optional;

import javax.swing.JToggleButton;
import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.core.pure.ClothoidPlan;
import ch.ethz.idsc.gokart.core.pure.ClothoidPursuitConfig;
import ch.ethz.idsc.gokart.core.pure.CurveClothoidPursuitPlanner;
import ch.ethz.idsc.gokart.gui.top.TrajectoryDesignModule;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.planar.ClothoidTerminalRatios;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Nest;
import ch.ethz.idsc.tensor.sca.Sign;

public class ClothoidPlannerMockup extends TrajectoryDesignModule {
  private static final Scalar SPEED = Quantity.of(5, SI.VELOCITY);
  // ---
  private final CurveClothoidPursuitPlanner planner = new CurveClothoidPursuitPlanner();
  protected final JToggleButton jToggleButton = new JToggleButton("cloth");
  // ---
  protected Optional<ClothoidPlan> optional = Optional.empty();
  private Tensor mouseSe2 = Array.zeros(3);
  private final RenderInterface renderInterface = new RenderInterface() {
    public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
      if (jToggleButton.isSelected()) {
        mouseSe2 = geometricLayer.getMouseSe2State();
        if (optional.isPresent()) {
          Tensor refined = Nest.of(ClothoidTerminalRatios.CURVE_SUBDIVISION::string, optional.get().curve(), 3);
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
      trajectoryDesign.timerFrame.jToolBar.addSeparator();
      jToggleButton.setToolTipText("clothoid pursuit planner");
      jToggleButton.setSelected(false);
      // TODO enable/disable control point changes
      trajectoryDesign.timerFrame.jToolBar.add(jToggleButton);
    }
    {
      trajectoryDesign.timerFrame.geometricComponent.jComponent.addMouseListener(new MouseAdapter() {
        public void mousePressed(MouseEvent mouseEvent) {
          if (jToggleButton.isSelected() && mouseEvent.getButton() == 2) { // middle mouse button
            Timing timing = Timing.started();
            optional = planner.getPlan(PoseHelper.attachUnits(mouseSe2), SPEED, //
                trajectoryDesign.getRefinedCurve(), //
                Sign.isPositiveOrZero(SPEED), //
                ClothoidPursuitConfig.GLOBAL.trajectoryEntryFinder, //
                ClothoidPursuitConfig.ratioLimits());
            timing.stop();
            Scalar duration = Quantity.of(timing.seconds(), SI.SECOND);
            String msg = (optional.isPresent() ? "NEW" : "NO") + " clothoid plan found in " + duration;
            if (Scalars.lessThan(ClothoidPursuitConfig.GLOBAL.updatePeriod, duration))
              System.err.println(msg);
            else
              System.out.println(msg);
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
