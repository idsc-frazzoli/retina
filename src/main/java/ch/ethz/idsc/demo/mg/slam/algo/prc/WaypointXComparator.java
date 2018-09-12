// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.prc;

import java.util.Comparator;

/** compares way point in go kart frame based on x coordinate */
/* package */ enum WaypointXComparator implements Comparator<double[]> {
  INSTANCE;
  // ---
  @Override // from Comparator
  public int compare(double[] o1, double[] o2) {
    return Double.compare(o1[0], o2[0]);
  }
}
