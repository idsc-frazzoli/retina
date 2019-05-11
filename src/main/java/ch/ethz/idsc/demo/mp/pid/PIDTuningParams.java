// code by mcp
package ch.ethz.idsc.demo.mp.pid;

import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.owl.bot.se2.pid.PIDGains;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Clips;

public class PIDTuningParams {
  public static final PIDTuningParams GLOBAL = AppResources.load(new PIDTuningParams());
  // ---
  public Scalar Kp = Quantity.of(.5, "m^-2");
  public Scalar Ki = RealScalar.ZERO;
  public Scalar Kd = Quantity.of(4.0, "s*m^-2");
  public PIDGains pidGains = new PIDGains(Kp, Ki, Kd);
  // ---
  public final Scalar updatePeriod = Quantity.of(0.2, SI.SECOND); // 0.2[s] == 5[Hz]
  // ---
  public Scalar maxSteerAngleSafetyRatio = RealScalar.of(.9); // Avoid limit of actuator
  final Scalar maxSteerAngle = ChassisGeometry.GLOBAL.steerAngleForTurningRatio( //
      SteerConfig.GLOBAL.turningRatioMax);

  // TODO MCP not yet used. is this needed?
  public final Clip clipAngle() {
    return Clips.interval( //
        maxSteerAngle.multiply(maxSteerAngleSafetyRatio).negate(), //
        maxSteerAngle.multiply(maxSteerAngleSafetyRatio));
  }
}
