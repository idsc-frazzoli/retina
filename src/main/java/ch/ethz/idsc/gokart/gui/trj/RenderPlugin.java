// code by jph
package ch.ethz.idsc.gokart.gui.trj;

import ch.ethz.idsc.owl.gui.RenderInterface;

interface RenderPlugin {
  RenderInterface renderInterface(RenderPluginParameters renderPluginParameters);
}
