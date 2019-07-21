// code by jph
package ch.ethz.idsc.gokart.gui.trj;

import java.awt.Color;

import ch.ethz.idsc.gokart.core.plan.TrajectoryConfig;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.EmptyRender;
import ch.ethz.idsc.owl.gui.ren.WaypointRender;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.ply.Arrowhead;
import ch.ethz.idsc.tensor.Tensor;

/* package */ enum WaypointsRenderPlugin implements RenderPlugin {
  INSTANCE;
  // ---
  @Override
  public RenderInterface renderInterface(RenderPluginParameters renderPluginParameters) {
    Tensor curve = renderPluginParameters.curve;
    if (1 < curve.length()) {
      CurveSubdivision curveSubdivision = Se2UniformResample.of(TrajectoryConfig.GLOBAL.waypointsSpacing);
      Tensor waypoints = curveSubdivision.cyclic(curve);
      waypoints = Tensor.of(waypoints.stream().map(PoseHelper::toUnitless));
      return new WaypointRender(Arrowhead.of(.5), new Color(0, 0, 255, 64)).setWaypoints(waypoints);
    }
    return EmptyRender.INSTANCE;
  }
}
