// code by jph
package ch.ethz.idsc.retina.util.math;

/** digests sequence of double values
 * 
 * tracks min and max of all values */
public class IntervalTracker {
  private double min = Double.POSITIVE_INFINITY;
  private double max = Double.NEGATIVE_INFINITY;
  private double last_value = Double.NaN;

  /** @param value provided for tracking */
  public void setValue(double value) {
    min = Math.min(min, value);
    max = Math.max(max, value);
    last_value = value;
  }

  /** when calling the function before any value was provided
   * -Infinity is returned.
   * 
   * @return width of min max range of provided double values so far */
  public double getWidth() {
    return max - min;
  }

  /** when calling the function before any value was provided
   * NaN is returned.
   * 
   * @return */
  public double getValueCentered() {
    return getWidth() == 0 ? 0 : last_value - (max + min) * 0.5; // offsetting to [-0.65, 0.65]
  }

  /** when calling the function before any value was provided
   * NaN is returned.
   * 
   * @return last double value that was provided */
  public double getValue() {
    return last_value;
  }
}
