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
    for (int index = 0; index < tensor.length(); ++index) {
      array.append(tensor.get(index));
      Tensor A = cyclic(tensor, index - 1);
      Tensor B = cyclic(tensor, index - 0);
      Tensor C = cyclic(tensor, index + 1);
      Tensor D = cyclic(tensor, index + 2);
      array.append(ics.midpoint(A, B, C, D));
    }
    return array;
  }

  private static Tensor cyclic(Tensor tensor, int index) {
    return tensor.get(mod(index, tensor.length()));
  }

  private static int mod(int index, int size) {
    int value = index % size;
    return value < 0 ? size + value : value;
  }
}
