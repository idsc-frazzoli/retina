// code by am
package ch.ethz.idsc.gokart.core.adas;

import java.util.Optional;

import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.tensor.Tensor;

/** class is used to develop and test anti lock brake logic */
public class LaneKeepingCenterlineModule extends AbstractModule {
  private Optional<Tensor> optionalCurve = Optional.empty();

  @Override // from AbstractModule
  protected void first() {
  }

  @Override // from AbstractModule
  protected void last() {
  }

  public void setCurve(Optional<Tensor> curve) {
    if (curve.isPresent()) {
      optionalCurve = curve;
    } else {
      System.err.println("Curve missing");
      optionalCurve = Optional.empty();
    }
  }

  final Optional<Tensor> getCurve() {
    System.out.println("got curve");
    return optionalCurve;
  }
}
