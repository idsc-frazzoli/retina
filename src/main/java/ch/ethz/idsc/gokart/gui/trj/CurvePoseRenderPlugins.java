// code by jph
package ch.ethz.idsc.gokart.gui.trj;

import ch.ethz.idsc.owl.gui.RenderInterface;

/* package */ enum CurvePoseRenderPlugins {
  CLOTHOID_PURSUIT(ClothoidPursuitRenderPlugin.INSTANCE), //
  LANE_CONSTRAINTS(LaneConstraintsRenderPlugin.INSTANCE), //
  ;
  private final RenderPlugin curvePoseRenderPlugin;

  CurvePoseRenderPlugins(RenderPlugin curvePoseRenderPlugin) {
    this.curvePoseRenderPlugin = curvePoseRenderPlugin;
  }

  public RenderInterface renderInterface(RenderPluginParameters renderPluginParameters) {
    return curvePoseRenderPlugin.renderInterface(renderPluginParameters);
  }
}
