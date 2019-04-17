// code by gjoel
package ch.ethz.idsc.gokart.core.pure;

import java.util.Optional;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

/** class is the default choice for geodesic pursuit when driving along a curve in global
 * coordinates while the pose is updated periodically from a localization method. */
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
          pursuitConfig.geodesicInterface, //
          pursuitConfig.trajectoryEntryFinder, //
          PursuitConfig.ratioLimits());
    System.err.println("no curve in geodesic pursuit");
    return Optional.empty();
  }
}
