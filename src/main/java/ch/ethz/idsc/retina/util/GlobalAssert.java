// code by jph
package ch.ethz.idsc.retina.util;

public enum GlobalAssert {
  ;
  /** throws an exception if valid == false
   * 
   * @param valid */
  public static void that(boolean valid) {
    if (!valid)
      throw new RuntimeException();
  }
}
