// code by mg
package ch.ethz.idsc.demo.mg.util;

import java.util.Random;

// methods to ease handling with random things in SLAM algorithm
public class SlamRandomUtil {
  private static Random generator = new Random();

  /** Gaussian distributed random variable with given mean and standard deviation
   * 
   * @param mean
   * @param standardDeviation
   * @return */
  public static double getGaussian(double mean, double standardDeviation) {
    return generator.nextGaussian() * standardDeviation + mean;
  }

  // set array with uniformly distributed random variables in interval [0,1]
  public static void setUniformRVArray(double[] randomArray) {
    for (int i = 0; i < randomArray.length; i++)
      randomArray[i] = SlamRandomUtil.getUniformRV();
  }

  // draws from uniformly distributed random variable in interval [0,1]
  public static double getUniformRV() {
    return generator.nextDouble();
  }
}
