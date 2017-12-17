// code by jph
package ch.ethz.idsc.retina.gui.gokart.crv;

import ch.ethz.idsc.retina.util.curve.CurveSubdivision;
import ch.ethz.idsc.retina.util.curve.FourPointSubdivision;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Nest;

public enum DubendorfCurve {
  ;
  public static final Tensor OVAL = oval();

  private static Tensor oval() {
    // TODO consider stating coordinates in [m]eters
    Tensor poly = Tensors.of( //
        Tensors.vector(35.200, 44.933), //
        Tensors.vector(49.867, 59.200), //
        Tensors.vector(57.200, 54.800), //
        Tensors.vector(49.200, 45.067), //
        Tensors.vector(40.800, 37.333));
    CurveSubdivision unaryOperator = new CurveSubdivision(FourPointSubdivision.SCHEME);
    return Nest.of(unaryOperator, poly, 5).unmodifiable();
  }
}
