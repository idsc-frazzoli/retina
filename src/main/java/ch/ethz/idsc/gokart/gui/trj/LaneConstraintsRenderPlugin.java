// code by jph
package ch.ethz.idsc.gokart.gui.trj;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
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
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ enum LaneConstraintsRenderPlugin implements RenderPlugin {
  INSTANCE;
  // ---
  @Override // from RenderPlugin
  public RenderInterface renderInterface(RenderPluginParameters renderPluginParameters) {
    if (1 < renderPluginParameters.laneBoundaryL.length() && //
        1 < renderPluginParameters.laneBoundaryR.length()) {
      Tensor pose = renderPluginParameters.pose;
      ClothoidPursuitConfig clothoidPursuitConfig = new ClothoidPursuitConfig();
      // large value is a hack to get a solution
      clothoidPursuitConfig.turningRatioMax = Quantity.of(1000, SI.PER_METER);
      Optional<ClothoidPlan> optionalL = //
          new CurveClothoidPursuitPlanner(clothoidPursuitConfig).getPlan(pose, Quantity.of(0, SI.VELOCITY), renderPluginParameters.laneBoundaryL, true);
      Optional<ClothoidPlan> optionalR = //
          new CurveClothoidPursuitPlanner(clothoidPursuitConfig).getPlan(pose, Quantity.of(0, SI.VELOCITY), renderPluginParameters.laneBoundaryR, true);
      return new ClothoidPursuitRender(pose, optionalL, optionalR);
    }
    return EmptyRender.INSTANCE;
  }

  // ---
  private static class ClothoidPursuitRender implements RenderInterface {
    private static final Font FONT = new Font(Font.MONOSPACED, Font.PLAIN, 14);
    // ---
    private final PathRender pathRenderL = new PathRender(new Color(255, 0, 128), 2f);
    private final PathRender pathRenderR = new PathRender(new Color(0, 255, 128), 2f);
    private final Tensor pose;
    private final Optional<ClothoidPlan> clothoidPlanL;
    private final Optional<ClothoidPlan> clothoidPlanR;

    private ClothoidPursuitRender(Tensor pose, Optional<ClothoidPlan> clothoidPlanL, Optional<ClothoidPlan> clothoidPlanR) {
      this.pose = pose;
      this.clothoidPlanL = clothoidPlanL;
      this.clothoidPlanR = clothoidPlanR;
    }

    @Override
    public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
      // draw footprint of gokart
      geometricLayer.pushMatrix(PoseHelper.toSE2Matrix(pose));
      graphics.setColor(new Color(128, 128, 128, 64));
      graphics.fill(geometricLayer.toPath2D(RimoSinusIonModel.standard().footprint()));
      geometricLayer.popMatrix();
      // ---
      graphics.setFont(FONT);
      if (clothoidPlanL.isPresent()) {
        ClothoidPlan clothoidPlan = clothoidPlanL.get();
        graphics.setColor(Color.BLACK);
        graphics.drawString("L MAX=" + clothoidPlan.ratio().map(Round._4), 0, 30);
        pathRenderL.setCurve(clothoidPlan.curve(), false).render(geometricLayer, graphics);
      } else
        System.out.println("no plan left");
      if (clothoidPlanR.isPresent()) {
        ClothoidPlan clothoidPlan = clothoidPlanR.get();
        graphics.setColor(Color.BLACK);
        graphics.drawString("R MAX=" + clothoidPlan.ratio().map(Round._4), 0, 50);
        pathRenderR.setCurve(clothoidPlan.curve(), false).render(geometricLayer, graphics);
      } else
        System.out.println("no plan right");
    }
  }
}
