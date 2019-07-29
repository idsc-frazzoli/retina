// code by jph
package ch.ethz.idsc.gokart.gui.trj;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pure.ClothoidPlan;
import ch.ethz.idsc.gokart.core.pure.ClothoidPursuitConfig;
import ch.ethz.idsc.gokart.core.pure.CurveClothoidPursuitPlanner;
import ch.ethz.idsc.owl.car.shop.RimoSinusIonModel;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.EmptyRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.CirclePoints;
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
    private static final Tensor CIRCLE_POINTS = CirclePoints.of(20).unmodifiable();
    // ---
    private final ClothoidPlan clothoidPlan;
    private final PathRender pathRender = new PathRender(new Color(255, 128, 0), 2f);

    private ClothoidPursuitRender(ClothoidPlan clothoidPlan) {
      this.clothoidPlan = clothoidPlan;
      pathRender.setCurve(clothoidPlan.curve(), false);
    }

    @Override
    public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
      geometricLayer.pushMatrix(PoseHelper.toSE2Matrix(clothoidPlan.startPose()));
      graphics.setColor(new Color(128, 128, 128, 64));
      graphics.fill(geometricLayer.toPath2D(RimoSinusIonModel.standard().footprint()));
      {
        Path2D path2d = geometricLayer.toPath2D(CIRCLE_POINTS.multiply(ClothoidPursuitConfig.GLOBAL.lookAhead));
        path2d.closePath();
        graphics.setColor(new Color(128, 128, 128, 128));
        graphics.draw(path2d);
      }
      geometricLayer.popMatrix();
      // ---
      pathRender.render(geometricLayer, graphics);
    }
  }
}
