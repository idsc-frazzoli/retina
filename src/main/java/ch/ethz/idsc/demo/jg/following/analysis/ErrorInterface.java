// code by gjoel
package ch.ethz.idsc.demo.jg.following.analysis;

import ch.ethz.idsc.tensor.Tensor;

public interface ErrorInterface {
  Tensor averageError();

  Tensor accumulatedError();

  String getReport();
}
