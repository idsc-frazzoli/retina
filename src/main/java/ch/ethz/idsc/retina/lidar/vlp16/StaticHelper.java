// code by jph
package ch.ethz.idsc.retina.lidar.vlp16;

/* package */ enum StaticHelper {
  ;
  /** @param laserId from the range {0, 1, 2, ..., 15}
   * @return */
  public static int degree(int laserId) {
    if (laserId < 0)
      throw new RuntimeException();
    if (laserId == 15)
      return 15;
    return -15 + laserId * 16 % 30;
  }

  /** @param degree in {-15, -13, -11, -1, +1, +3, ..., +15}
   * @return lidarId in the range {0, 1, 2, ..., 15} */
  public static int lidarId(int degree) {
    if (degree == 15)
      return 15;
    return (degree + 15) % 15;
  }
}
