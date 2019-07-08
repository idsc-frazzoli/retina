// code by jph
package ch.ethz.idsc.gokart.gui.trj;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pure.ClothoidPlan;
import ch.ethz.idsc.gokart.core.pure.ClothoidPursuitConfig;
import ch.ethz.idsc.gokart.core.pure.CurveClothoidPursuitPlanner;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.EmptyRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ enum ClothoidPursuitRenderPlugin implements RenderPlugin {
  INSTANCE;
  // ---
  @Override // from RenderPlugin
  public RenderInterface renderInterface(RenderPluginParameters renderPluginParameters) {
    Tensor curve = renderPluginParameters.curve;
    if (1 < curve.length()) {
      Tensor pose = renderPluginParameters.pose;
      CurveClothoidPursuitPlanner curveClothoidPursuitPlanner = new CurveClothoidPursuitPlanner(ClothoidPursuitConfig.GLOBAL);
      Optional<ClothoidPlan> optional = curveClothoidPursuitPlanner.getPlan(pose, Quantity.of(0, SI.VELOCITY), curve, true);
      if (optional.isPresent())
        return new ClothoidPursuitRender(optional.get());
    }
    return EmptyRender.INSTANCE;
  }

  // ---
  private static class ClothoidPursuitRender implements RenderInterface {
    private final PathRender pathRender = new PathRender(new Color(255, 0, 128));
    private final ClothoidPlan clothoidPlan;

    private ClothoidPursuitRender(ClothoidPlan clothoidPlan) {
      this.clothoidPlan = clothoidPlan;
    }

    @Override
    public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
      pathRender.setCurve(clothoidPlan.curve(), false).render(geometricLayer, graphics);
    }
  }
}
