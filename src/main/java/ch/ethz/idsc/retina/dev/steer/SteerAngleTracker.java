// code by jph
package ch.ethz.idsc.retina.dev.steer;

public class SteerAngleTracker implements SteerGetListener {
  private double min = +1e10;
  private double max = -1e10;

  public double getSteeringAngle(SteerGetEvent steerGetEvent) {
    getEvent(steerGetEvent);
    double width = width();
    if (width == 0)
      return 0;
    double angle = steerGetEvent.getSteeringAngle();
    return (2 * angle - max - min) / width;
  }

  public double width() {
    return max - min;
  }

  @Override
  public void getEvent(SteerGetEvent steerGetEvent) {
    double angle = steerGetEvent.getSteeringAngle();
    min = Math.min(min, angle);
    max = Math.max(max, angle);
  }
}
