// code by edo and jph
package ch.ethz.idsc.retina.dev.steer;

public class PDSteerPositionControl {
  private final double dt = (SteerSocket.SEND_PERIOD_MS * 1e-3);
  /** pos error initially incorrect in the first iteration */
  private double lastPos_error = 0;

  public double iterate(final double pos_error) {
    double Kp = SteerConfig.GLOBAL.Kp.number().doubleValue(); // 5
    double Kd = SteerConfig.GLOBAL.Kd.number().doubleValue();
    double torqueLimit = SteerConfig.GLOBAL.torqueLimit.number().doubleValue();
    // ---
    // TODO computation using Scalars
    double pPart = pos_error * Kp;
    double dPart = (pos_error - lastPos_error) * Kd / dt;
    lastPos_error = pos_error;
    double control = pPart + dPart;
    control = Math.min(+torqueLimit, control);
    control = Math.max(-torqueLimit, control);
    return control;
  }
}
