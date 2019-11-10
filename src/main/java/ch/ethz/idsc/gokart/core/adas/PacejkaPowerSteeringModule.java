// code by jph
package ch.ethz.idsc.gokart.core.adas;

public class PacejkaPowerSteeringModule extends PowerSteeringModule {
  public PacejkaPowerSteeringModule() {
    super(new PacejkaPowerSteering(HapticSteerConfig.GLOBAL));
  }
}
