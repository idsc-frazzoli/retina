// code by jph
package ch.ethz.idsc.retina.dev.steer;

public class SteerAngleTracker implements SteerGetListener {
  private double min = +1e10;
  private double max = -1e10;
  private double currAngle = 0;

  public double getSteeringAngleRelative(SteerGetEvent steerGetEvent) {
    getEvent(steerGetEvent);
    double width = width();
    if (width == 0)
      return 0;
    double angle = steerGetEvent.getSteeringAngle();
    return (2 * angle - max - min) / width;
  }

  private double getSteeringAngle(SteerGetEvent steerGetEvent) {
    //getEvent(steerGetEvent);
    double width = width();
    if (width == 0)
      return 0;
    double angle = steerGetEvent.getSteeringAngle();
    return angle - (max + min)*0.5; //offsetting to [-0.65, 0.65]
  }

  
  public double getCurrAngle() {
    return currAngle;
  }
  
  public double width() {
    return max - min;
  }

  @Override
  public void getEvent(SteerGetEvent steerGetEvent) {
    double angle = steerGetEvent.getSteeringAngle();
    min = Math.min(min, angle);
    max = Math.max(max, angle);
    currAngle = getSteeringAngle(steerGetEvent);
  }
}
