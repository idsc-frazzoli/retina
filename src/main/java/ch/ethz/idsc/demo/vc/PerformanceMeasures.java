// code by vc
package ch.ethz.idsc.demo.vc;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

/* package */ class PerformanceMeasures {
  public final double precision;
  public final double recall;

  public PerformanceMeasures(double recall, double precision) {
    this.precision = precision;
    this.recall = recall;
  }

  public Tensor toTensor() {
    return Tensors.vector(precision, recall);
  }

  @Override
  public String toString() {
    return String.format("recall   =%6.3f\nprecision=%6.3f", recall, precision);
  }
}
