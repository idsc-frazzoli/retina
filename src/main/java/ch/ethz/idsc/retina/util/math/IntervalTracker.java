// code by jph
package ch.ethz.idsc.retina.util.math;

public class IntervalTracker {
  private double min = Double.POSITIVE_INFINITY;
  private double max = Double.NEGATIVE_INFINITY;
  private double last_value = 0;

  public double getValueCentered() {
    return getWidth() == 0 ? 0 : last_value - (max + min) * 0.5; // offsetting to [-0.65, 0.65]
  }

  public double getValue() {
    return last_value;
  }

  public double getWidth() {
    return max - min;
  }

  public void setValue(double value) {
    min = Math.min(min, value);
    max = Math.max(max, value);
    last_value = value;
  }
}
