// code by jph
package ch.ethz.idsc.gokart.gui.trj;

import ch.ethz.idsc.tensor.Tensor;

/* package */ class RenderPluginParameters {
  public final Tensor curve;
  public final Tensor pose;
  // add variables if needed. they do not have to be final:
  public int some_value;

  public RenderPluginParameters(Tensor curve, Tensor pose) {
    this.curve = curve;
    this.pose = pose;
  }
}
