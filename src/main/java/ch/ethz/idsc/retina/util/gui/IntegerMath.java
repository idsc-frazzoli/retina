// code by jph
package ch.ethz.idsc.retina.util.gui;

import java.util.Collection;

enum IntegerMath {
  ;
  /** mod that behaves like in Matlab. for instance mod(-10, 3) == 2
   * 
   * @param index
   * @param size
   * @return matlab.mod(index, size) */
  public static int mod(int index, int size) {
    int value = index % size;
    // if value is below 0, then -size < value && value < 0.
    // For instance: -3%3==0, and -2%3==-2.
    return value < 0 ? size + value : value;
  }

  /** Euclid's algorithm
   * 
   * @param a
   * @param b
   * @return greatest common divider of a and b. */
  public static int gcd(int a, int b) {
    return b == 0 ? a : gcd(b, a % b);
  }

  public static int lcm(int a, int b) {
    return a * (b / gcd(a, b)); // to avoid overflow
  }

  /** @param myCollection non-empty
   * @return greatest common divider of all integers in myCollection */
  public static int gcd(Collection<Integer> myCollection) {
    return myCollection.stream().reduce(IntegerMath::gcd).orElse(null);
  }

  /** @param myCollection non-empty
   * @return least common multiple of all integers in myCollection */
  public static int lcm(Collection<Integer> myCollection) {
    return myCollection.stream().reduce(IntegerMath::lcm).orElse(null);
  }
}
