// code by jph
package ch.ethz.idsc.gokart.gui.trj;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.util.Optional;

import ch.ethz.idsc.gokart.core.pure.CurvePurePursuitHelper;
import ch.ethz.idsc.gokart.core.pure.PurePursuitConfig;
import ch.ethz.idsc.owl.bot.se2.Se2CarIntegrator;
import ch.ethz.idsc.owl.bot.se2.Se2StateSpaceModel;
import ch.ethz.idsc.owl.car.shop.RimoSinusIonModel;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.EmptyRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.StateSpaceModels;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.state.FixedStateIntegrator;
import ch.ethz.idsc.owl.math.state.StateIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.sca.N;
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
    private static final StateTime CENTER = new StateTime(Array.zeros(3), RealScalar.ZERO);
    private static final StateIntegrator STATE_INTEGRATOR = FixedStateIntegrator.create( //
        Se2CarIntegrator.INSTANCE, RationalScalar.of(1, 4), 4 * 5);
    // ---
    private final Tensor pose;
    private final Scalar ratio;
    private final Flow flow_forward;
    private final PathRender pathRender = new PathRender(new Color(255, 128, 0), 2f);

    private PurePursuitRender(Tensor pose, Scalar ratio) {
      this.pose = pose;
      this.ratio = ratio;
      flow_forward = singleton(RealScalar.ONE, Magnitude.PER_METER.apply(ratio));
      pathRender.setCurve(Tensor.of(STATE_INTEGRATOR.trajectory(CENTER, flow_forward).stream().map(StateTime::state)), false);
    }

    @Override
    public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
      geometricLayer.pushMatrix(PoseHelper.toSE2Matrix(pose));
      graphics.setColor(new Color(128, 128, 128, 64));
      graphics.fill(geometricLayer.toPath2D(RimoSinusIonModel.standard().footprint()));
      {
        graphics.setColor(new Color(128, 128, 128, 128));
        graphics.draw(geometricLayer.toPath2D(CIRCLE_POINTS.multiply(PurePursuitConfig.GLOBAL.lookAhead), true));
      }
      pathRender.render(geometricLayer, graphics);
      geometricLayer.popMatrix();
      // ---
      graphics.setFont(FONT);
      graphics.setColor(Color.DARK_GRAY);
      graphics.drawString("" + ratio.map(Round._4), 0, 50);
    }

    /** @param speed [m*s^-1]
     * @param ratio [m^-1]
     * @return */
    /* package for testing */ static Flow singleton(Scalar speed, Tensor ratio) {
      return StateSpaceModels.createFlow(Se2StateSpaceModel.INSTANCE, //
          N.DOUBLE.of(Tensors.of(speed, speed.zero(), ratio.multiply(speed))));
    }
  }
}
