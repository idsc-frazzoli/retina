// code by jph
package ch.ethz.idsc.gokart.gui.trj;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.ethz.idsc.gokart.core.plan.TrajectoryConfig;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.EmptyRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.retina.util.spline.BSpline2Vector;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum MpcTrackRenderPlugin implements RenderPlugin {
  INSTANCE;
  // ---
  @Override // from RenderPlugin
  public RenderInterface renderInterface(RenderPluginParameters renderPluginParameters) {
    if (1 < renderPluginParameters.laneBoundaryL.length() && //
        1 < renderPluginParameters.laneBoundaryR.length()) {
      Tensor bases = Tensor.of(TrajectoryConfig.GLOBAL.resampledWaypoints(renderPluginParameters.curve, true).stream() //
          .map(PoseHelper::toUnitless) //
          .map(Extract2D.FUNCTION));
      return new MpcTrackRender(bases);
    }
    return EmptyRender.INSTANCE;
  }

  // ---
  private static class MpcTrackRender implements RenderInterface {
    private static final PathRender PATH_RENDER = new PathRender(new Color(128, 128, 128), 1.5f);
    // ---
    // private final Tensor bases;
    private final RenderInterface renderInterface;

    private MpcTrackRender(Tensor bases) {
      // this.bases = bases;
      // TODO code redundant to TrackRefinement
      boolean cyclic = true;
      int resolution = 8;
      final int n = bases.length();
      Tensor domain = Tensors.vector(i -> RealScalar.of(i / (double) resolution), (cyclic ? n : n - 2) * resolution);
      Tensor matrixD0 = domain.map(BSpline2Vector.of(n, 0, cyclic));
      // Tensor matrixD1 = domain.map(BSpline2Vector.of(n, 1, cyclic));
      renderInterface = PATH_RENDER.setCurve(matrixD0.dot(bases), true);
      // TrackRefinement
    }

    @Override
    public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
      renderInterface.render(geometricLayer, graphics);
    }
  }
}
