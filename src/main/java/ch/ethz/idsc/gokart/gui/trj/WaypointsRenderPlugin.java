// code by jph
package ch.ethz.idsc.gokart.gui.trj;

import java.awt.Color;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.EmptyRender;
import ch.ethz.idsc.owl.gui.ren.WaypointRender;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.sophus.itp.GeodesicInterpolation;
import ch.ethz.idsc.sophus.lie.se2.Se2Geodesic;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.sophus.ply.Arrowhead;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.opt.Interpolation;
import ch.ethz.idsc.tensor.pdf.Distribution;
import ch.ethz.idsc.tensor.pdf.EmpiricalDistribution;
import ch.ethz.idsc.tensor.pdf.InverseCDF;
import ch.ethz.idsc.tensor.red.Norm;

/* package */ enum WaypointsRenderPlugin implements RenderPlugin {
  INSTANCE;
  // ---
  @Override
  public RenderInterface renderInterface(RenderPluginParameters renderPluginParameters) {
    Tensor curve = Tensor.of(renderPluginParameters.curve.stream().map(PoseHelper::toUnitless));
    if (1 < curve.length()) {
      Tensor diffs = Tensor.of(Differences.of(curve).stream().map(Extract2D.FUNCTION).map(Norm._2::ofVector));
      Interpolation interpolation = GeodesicInterpolation.of(Se2Geodesic.INSTANCE, curve);
      Distribution distribution = EmpiricalDistribution.fromUnscaledPDF(diffs);
      InverseCDF inverseCDF = (InverseCDF) distribution;
      Tensor map = Subdivide.of(0, 1, 10).map(inverseCDF::quantile);
      Tensor waypoints = map.map(interpolation::at);
      return new WaypointRender(Arrowhead.of(.5), Color.BLUE).setWaypoints(waypoints);
    }
    return EmptyRender.INSTANCE;
  }
}
