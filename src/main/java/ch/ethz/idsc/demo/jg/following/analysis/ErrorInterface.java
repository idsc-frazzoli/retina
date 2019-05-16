// code by gjoel
package ch.ethz.idsc.demo.jg.following.analysis;

import java.util.Optional;

import ch.ethz.idsc.tensor.Tensor;

public interface ErrorInterface {
  Optional<Tensor> averageError();

  Optional<Tensor> accumulatedError();

  Optional<String> getReport();
}
