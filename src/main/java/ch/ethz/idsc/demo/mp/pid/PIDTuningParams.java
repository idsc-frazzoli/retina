package ch.ethz.idsc.demo.mp.pid;

import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AppResources;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public class PIDTuningParams {
  public static final PIDTuningParams GLOBAL = AppResources.load(new PIDTuningParams());
  public Scalar pGain = Quantity.of(.1, SI.ONE);
  public Scalar pGainPose = Quantity.of(1.0, SI.PER_METER);
  public Scalar iGain = Quantity.of(1.0, SI.PER_SECOND);
  public Scalar dGain = Quantity.of(10.0, SI.ONE);
  public final Scalar updatePeriod = Quantity.of(0.1, SI.SECOND); // 0.1[s] == 10[Hz]

  /** @return unitless p gain */
  public Scalar getPGain() {
    return Magnitude.PER_METER.apply(pGain);
  }

  /** @return unitless i gain */
  public Scalar getIGain() {
    return Magnitude.ONE.apply(iGain);
  }

  /** @return unitless d gain */
  public Scalar getDGain() {
    return Magnitude.ONE.apply(dGain);
  }
}
