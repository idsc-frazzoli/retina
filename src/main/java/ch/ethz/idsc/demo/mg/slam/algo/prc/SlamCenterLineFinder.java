// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.prc;

import ch.ethz.idsc.tensor.Tensor;

// utils to find the race track center line from the detected boundaries
/* package */ enum SlamCenterLineFinder {
  ;
  // offsets the given curve by a constant Tensor offset
  public static void offSetCurve(Tensor boundary, Tensor offset) {
    for (int i = 0; i < boundary.length(); ++i) {
      boundary.set(boundary.get(i).add(offset), i);
    }
  }
  // TODO method which gets two boundaries as input
}
