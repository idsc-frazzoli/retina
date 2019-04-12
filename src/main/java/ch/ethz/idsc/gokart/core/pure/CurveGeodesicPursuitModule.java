// code by gjoel
package ch.ethz.idsc.gokart.core.pure;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

import java.util.Optional;

public class CurveGeodesicPursuitModule extends CurvePurePursuitModule {

  public CurveGeodesicPursuitModule(PursuitConfig pursuitConfig) {
    super(pursuitConfig);
  }

  @Override // from CurvePurePursuitModule
  protected synchronized Optional<Scalar> getRatio(Tensor pose) {
    Optional<Tensor> optionalCurve = this.optionalCurve; // copy reference instead of synchronize
    if (optionalCurve.isPresent())
      return CurveGeodesicPursuitHelper.getRatio( //
          pose, //
          speed, //
          optionalCurve.get(), //
          isForward, //
          pursuitConfig.geodesic, //
          pursuitConfig.entryFinder, //
          pursuitConfig.ratioLimits);
    System.err.println("no curve in geodesic pursuit");
    return Optional.empty();
  }
}
