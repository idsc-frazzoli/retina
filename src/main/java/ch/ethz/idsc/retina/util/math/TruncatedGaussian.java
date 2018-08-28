// code by mg
package ch.ethz.idsc.retina.util.math;

import java.util.Random;

public class TruncatedGaussian {
  private static final int LIMIT = 50;
  private static final Random RANDOM = new Random();
  // ---
  private final double mean;
  private final double standardDeviation;
  private final double lowerBound;
  private final double upperBound;

  /** truncated Gaussian distribution obtained with rejection sampling. Returned random values will lie
   * between lowerBound and upperBound
   * 
   * @param mean
   * @param standardDeviation
   * @param lowerBound
   * @param upperBound */
  public TruncatedGaussian(double mean, double standardDeviation, double lowerBound, double upperBound) {
    this.mean = mean;
    this.standardDeviation = standardDeviation;
    this.lowerBound = lowerBound;
    this.upperBound = upperBound;
  }

  /** @return
   * @throws Exception */
  public double nextValue() {
    for (int count = 0; count < LIMIT; ++count) {
      double value = getGaussian();
      if (lowerBound <= value && value <= upperBound)
        return value;
    }
    throw new RuntimeException();
  }

  /** Gaussian distributed random variable with given mean and standard deviation
   * 
   * @param mean
   * @param standardDeviation */
  private double getGaussian() {
    return RANDOM.nextGaussian() * standardDeviation + mean;
  }
}
