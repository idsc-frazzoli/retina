// code by jph
package ch.ethz.idsc.retina.util.math;

public class IntervalTracker {
  private double min = +1e10;
  private double max = -1e10;
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

  public void setValue(double angle) {
    min = Math.min(min, angle);
    max = Math.max(max, angle);
    last_value = angle;
  }
}
