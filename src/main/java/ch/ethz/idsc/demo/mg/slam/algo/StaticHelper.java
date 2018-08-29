// code by mg
package ch.ethz.idsc.demo.mg.slam.algo;

import java.util.Random;

/** methods to ease handling with random variables in SLAM algorithm */
/* package */ enum StaticHelper {
  ;
  private static final Random RANDOM = new Random();

  /** fills array with random variables uniformly distributed in [0, 1]
   * 
   * @param */
  public static void setUniformRVArray(double[] randomArray) {
    for (int i = 0; i < randomArray.length; i++)
      randomArray[i] = getUniformRV();
  }

  /** @return uniformly distributed random variable in interval [0, 1] */
  private static double getUniformRV() {
    return RANDOM.nextDouble();
  }
}
