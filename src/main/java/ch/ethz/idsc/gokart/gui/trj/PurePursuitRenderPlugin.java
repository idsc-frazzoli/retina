// code by jph
package ch.ethz.idsc.gokart.gui.trj;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pure.CurvePurePursuitHelper;
import ch.ethz.idsc.gokart.core.pure.PurePursuitConfig;
import ch.ethz.idsc.owl.car.shop.RimoSinusIonModel;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.EmptyRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ enum PurePursuitRenderPlugin implements RenderPlugin {
  INSTANCE;
  // ---
  @Override // from RenderPlugin
  public RenderInterface renderInterface(RenderPluginParameters renderPluginParameters) {
    Tensor curve = renderPluginParameters.curve;
    if (1 < curve.length()) {
      Tensor pose = renderPluginParameters.pose;
      Optional<Scalar> optional = CurvePurePursuitHelper.getRatio(pose, curve, true, PurePursuitConfig.GLOBAL.lookAhead);
      if (optional.isPresent())
        return new PurePursuitRender(pose, optional.get());
    }
    return EmptyRender.INSTANCE;
  }

  // ---
  private static class PurePursuitRender implements RenderInterface {
    private static final Font FONT = new Font(Font.MONOSPACED, Font.PLAIN, 14);
    private static final Tensor CIRCLE_POINTS = CirclePoints.of(20).unmodifiable();
    // ---
    private final Tensor pose;
    private final Scalar ratio;

    private PurePursuitRender(Tensor pose, Scalar ratio) {
      this.pose = pose;
      this.ratio = ratio;
    }

    @Override
    public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
      geometricLayer.pushMatrix(PoseHelper.toSE2Matrix(pose));
      graphics.setColor(new Color(128, 128, 128, 64));
      graphics.fill(geometricLayer.toPath2D(RimoSinusIonModel.standard().footprint()));
      {
        Path2D path2d = geometricLayer.toPath2D(CIRCLE_POINTS.multiply(PurePursuitConfig.GLOBAL.lookAhead));
        path2d.closePath();
        graphics.setColor(new Color(128, 128, 128, 128));
        graphics.draw(path2d);
      }
      geometricLayer.popMatrix();
      // ---
      graphics.setFont(FONT);
      graphics.setColor(Color.DARK_GRAY);
      graphics.drawString("" + ratio.map(Round._4), 0, 50);
    }
  }
}
