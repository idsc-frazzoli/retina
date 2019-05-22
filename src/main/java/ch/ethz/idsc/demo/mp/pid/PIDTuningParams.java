// code by mcp
package ch.ethz.idsc.demo.mp.pid;

import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
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
  public Scalar Kp = Quantity.of(1, "m^-2");
  public Scalar Ki = RealScalar.ZERO;
  public Scalar Kd = Quantity.of(5, "s*m^-2");
  public PIDGains pidGains = new PIDGains(Kp, Ki, Kd);
  // ---
  public final Scalar updatePeriod = Quantity.of(0.05, SI.SECOND); // 0.05[s] == 20[Hz]
  // ---
  public Scalar maxSteerTurningRatioSafetyFactor = RealScalar.of(.9); // Avoid limit of actuator
  public Scalar maxSteerTurningRatio = //
      SteerConfig.GLOBAL.turningRatioMax.multiply(maxSteerTurningRatioSafetyFactor);
  public final Clip clipRatio() {
    return Clips.absolute(maxSteerTurningRatio);
  }
}
