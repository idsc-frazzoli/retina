// code by edo and jph
package ch.ethz.idsc.retina.dev.steer;

public class PDSteerPositionControl {
  public double Kp = 2.5; // 5
  public double Kd = 0.2; // 0.5 , 5 and 0.5 hit the saturation limit of 0.5
  public double torqueLimit = 0.5;
  // ---
  // TODO EJDH use realtime (with cap at 0.05?)
  private double dt = 0.020;
  // TODO EJDH probably not serious problem:
  // pos error initially incorrect in the first iteration
  private double lastPos_error = 0;

  public double iterate(final double pos_error) {
    double pPart = pos_error * Kp;
    double dPart = (pos_error - lastPos_error) * Kd / dt;
    lastPos_error = pos_error;
    double control = pPart + dPart;
    control = Math.min(+torqueLimit, control);
    control = Math.max(-torqueLimit, control);
    return control;
  }
}
