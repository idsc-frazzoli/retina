// code by jph
package ch.ethz.idsc.gokart.gui.trj;

import ch.ethz.idsc.owl.gui.RenderInterface;

/* package */ enum RenderPlugins {
  PURE_PURSUIT(PurePursuitRenderPlugin.INSTANCE), //
  CLOTHOID_PURSUIT(ClothoidPursuitRenderPlugin.INSTANCE), //
  LANE_CONSTRAINTS(LaneConstraintsRenderPlugin.INSTANCE), //
  WAYPOINTS(WaypointsRenderPlugin.INSTANCE), //
  ;
  private final RenderPlugin renderPlugin;

  RenderPlugins(RenderPlugin renderPlugin) {
    this.renderPlugin = renderPlugin;
  }

  public RenderInterface renderInterface(RenderPluginParameters renderPluginParameters) {
    return renderPlugin.renderInterface(renderPluginParameters);
  }
}
