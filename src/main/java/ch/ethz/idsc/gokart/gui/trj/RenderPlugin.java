// code by jph
package ch.ethz.idsc.gokart.gui.trj;

import ch.ethz.idsc.owl.gui.RenderInterface;

/** factory for RenderInterface */
@FunctionalInterface
interface RenderPlugin {
  /** @param renderPluginParameters
   * @return */
  RenderInterface renderInterface(RenderPluginParameters renderPluginParameters);
}
