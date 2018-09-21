// code by mg
package ch.ethz.idsc.demo.mg.slam;

import java.util.Comparator;

import ch.ethz.idsc.tensor.Tensor;

/** compares way point in go kart frame based on x coordinate */
/* package */ enum WaypointXComparator implements Comparator<Tensor> {
  INSTANCE;
  // ---
  @Override // from Comparator
  public int compare(Tensor o1, Tensor o2) {
    return Double.compare(o1.Get(0).number().doubleValue(), o2.Get(0).number().doubleValue());
  }
}
