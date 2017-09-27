// code by edo and jph
package ch.ethz.idsc.retina.dev.steer;

public class PDSteerPositionControl {
  double Kp = 0.0001 * 500;
  double Kd = 0.0014 * 500;
  double dt = 0.020; // TODO NRJ use realtime
  double lastPos_error = 0; // TODO NRJ pos error initially incorrect in the first iteration
  double torqueLimit = 0.5;

  public double iterate(final double pos_error) {
    double pPart = pos_error * Kp;
    double dPart = (pos_error - lastPos_error) * Kd / dt;
    lastPos_error = pos_error;
    final double sum = pPart + dPart;
    double control = sum;
    control = Math.min(+torqueLimit, control);
    control = Math.max(-torqueLimit, control);
    return control;
  }
}
