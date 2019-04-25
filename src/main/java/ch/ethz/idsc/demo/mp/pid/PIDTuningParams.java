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
  public Scalar Kp = Quantity.of(.1, SI.PER_METER);
  public Scalar Ki = RealScalar.ZERO;
  public Scalar Kd = Quantity.of(10.0, "s*m^-1");
  public PIDGains pidGains = new PIDGains(Kp, Ki, Kd);
  public final Scalar updatePeriod = Quantity.of(0.1, SI.SECOND); // 0.1[s] == 10[Hz]
  // ---
  Scalar maxSteerAngle = ChassisGeometry.GLOBAL.steerAngleForTurningRatio( //
      SteerConfig.GLOBAL.turningRatioMax);
  Scalar maxSteerAngleSafetyRatio = RealScalar.of(.9); //Avoid limit of actuator
  public final Clip clip = Clips.interval( //
      maxSteerAngle.negate().multiply(maxSteerAngleSafetyRatio), //
      maxSteerAngle.multiply(maxSteerAngleSafetyRatio)); 
}
