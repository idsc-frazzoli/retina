// code by mg
package ch.ethz.idsc.demo.mg.util.slam;

import java.util.Random;

/** methods to ease handling with random things in SLAM algorithm */
public enum SlamRandomUtil {
  ;
  private static final Random RANDOM = new Random();

  /** truncated Gaussian distribution obtained with rejection sampling
   * 
   * @param mean
   * @param standardDeviation
   * @param lowerBound
   * @param upperBound
   * @return */
  public static double getTruncatedGaussian(double mean, double standardDeviation, double lowerBound, double upperBound) {
    while (true) {
      double trunctatedGaussian = getGaussian(mean, standardDeviation);
      if (lowerBound <= trunctatedGaussian && trunctatedGaussian <= upperBound)
        return trunctatedGaussian;
    }
  }

  /** fills array with random variables uniformly distributed in [0, 1]
   * 
   * @param */
  public static void setUniformRVArray(double[] randomArray) {
    for (int i = 0; i < randomArray.length; i++)
      randomArray[i] = getUniformRV();
  }

  /** Gaussian distributed random variable with given mean and standard deviation
   * 
   * @param mean
   * @param standardDeviation
   * @return */
  private static double getGaussian(double mean, double standardDeviation) {
    return RANDOM.nextGaussian() * standardDeviation + mean;
  }

  /** @return uniformly distributed random variable in interval [0, 1] */
  private static double getUniformRV() {
    return RANDOM.nextDouble();
  }
}
