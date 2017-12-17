// code by jph
package ch.ethz.idsc.retina.util.curve;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class CurveSubdivision implements TensorUnaryOperator {
  private final InterpolatingCurveSubdivision ics;

  public CurveSubdivision(InterpolatingCurveSubdivision ics) {
    this.ics = ics;
  }

  @Override // from TensorUnaryOperator
  public Tensor apply(Tensor tensor) {
    Tensor array = Tensors.empty();
    PeriodicExtract periodicExtract = new PeriodicExtract(tensor);
    for (int index = 0; index < tensor.length(); ++index) {
      array.append(tensor.get(index));
      Tensor A = periodicExtract.get(index - 1);
      Tensor B = periodicExtract.get(index - 0);
      Tensor C = periodicExtract.get(index + 1);
      Tensor D = periodicExtract.get(index + 2);
      array.append(ics.midpoint(A, B, C, D));
    }
    return array;
  }
}
