package ch.ethz.idsc.gokart.core.pure;

import ch.ethz.idsc.retina.sys.AppResources;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;

public class PlanSRConfig {
  public static final PlanSRConfig GLOBAL = AppResources.load(new PlanSRConfig());
  // ---
  public Boolean SR_PED_LEGAL = true;
  public Boolean SR_PED_ILLEGAL = false;
  public Scalar PED_VELOCITY = RealScalar.of(1.6);
  public Scalar CAR_VELOCITY = RealScalar.of(10);
  public Scalar PED_RADIUS = RealScalar.of(0.3);
  public Scalar MAX_A = RealScalar.of(5.0); // [m/s²]
  public Scalar REACTION_TIME = RealScalar.of(0.3);
}
