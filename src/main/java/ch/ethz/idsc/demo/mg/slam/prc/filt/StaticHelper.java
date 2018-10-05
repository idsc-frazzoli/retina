// code by mg
package ch.ethz.idsc.demo.mg.slam.prc.filt;

enum StaticHelper {
  ;
  /** @param values
   * @return number of true in given values */
  static int filterCount(boolean[] values) {
    int count = 0;
    for (boolean validity : values)
      if (validity)
        ++count;
    return count;
  }
}
