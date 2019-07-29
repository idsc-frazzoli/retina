// code by jph
package ch.ethz.idsc.gokart.gui.trj;

import ch.ethz.idsc.tensor.Tensor;

/* package */ class RenderPluginParameters {
  public final Tensor curve;
  public final Tensor pose;
  // ---
  public Tensor laneBoundaryL;
  public Tensor laneBoundaryR;
  // add variables if needed. they do not have to be final:

  public RenderPluginParameters(Tensor curve, Tensor pose) {
    this.curve = curve;
    this.pose = pose;
  }
}
