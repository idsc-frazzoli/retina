// code by mg
package ch.ethz.idsc.demo.mg.slam.algo.prc;

import ch.ethz.idsc.tensor.Tensor;

// utils to find the race track center line from the detected boundaries
/* package */ enum SlamCenterLineFinder {
  ;
  /** offsets the given curve by a constant Tensor offset
   * 
   * @param boundary matrix N x 2
   * @param offset vector of length 2
   * @return all points in boundary shifted by given offset */
  // TODO not
  public static Tensor offsetCurve(Tensor boundary, Tensor offset) {
    // for (int i = 0; i < boundary.length(); ++i)
    // boundary.set(boundary.get(i).add(offset), i);
    // TODO try simpler version:
    // boundary.set(p -> p.add(offset), Tensor.ALL);
    return Tensor.of(boundary.stream().map(p -> p.add(offset)));
  }
  // TODO method which gets two boundaries as input
}
