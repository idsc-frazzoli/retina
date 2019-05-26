// code by jph
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public class CurvePurePursuitModule extends CurvePursuitModule {
  public CurvePurePursuitModule(PurePursuitConfig pursuitConfig) {
    super(pursuitConfig);
  }

  @Override
  protected final synchronized Optional<Scalar> getRatio(Tensor pose) {
    Optional<Tensor> optionalCurve = this.optionalCurve; // copy reference instead of synchronize
    if (optionalCurve.isPresent())
      return CurvePurePursuitHelper.getRatio( //
          pose, //
          optionalCurve.get(), //
          closed, //
          isForward, //
          pursuitConfig.lookAhead);
    System.err.println("no curve in pure pursuit");
    return Optional.empty();
  }
}
