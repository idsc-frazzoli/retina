// code by rvmoos and jph
package ch.ethz.idsc.retina.dev.steer;

public class PIDSteerPositionControl {
  double integral = 0;
  double dt = 0.020; // TODO NRJ use realtime
  double lastPos_error = 0; // TODO NRJ pos error initially incorrect in the first iteration
  double torqueLimit = 0.3;
  double antiResetWindup = 0;

  public double iterate(final double pos_error) {
    double pPart = pos_error * 3.5;
    double iPart = pos_error * 2.8 + antiResetWindup;
    double dPart = (pos_error - lastPos_error) * 0.3 / dt;
    lastPos_error = pos_error;
    integral += iPart * dt;
    final double sum = pPart + integral + dPart;
    double control = sum;
    control = Math.min(+torqueLimit, control);
    control = Math.max(-torqueLimit, control);
    antiResetWindup = (control - sum) * 0.7;
    return control;
  }
}
