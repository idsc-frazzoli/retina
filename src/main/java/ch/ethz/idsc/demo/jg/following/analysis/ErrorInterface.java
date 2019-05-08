// code by gjoel
package ch.ethz.idsc.demo.jg.following.analysis;

import ch.ethz.idsc.tensor.Scalar;

public interface ErrorInterface {

  Scalar averageError();

  Scalar accumulatedError();

  String getReport();
}
