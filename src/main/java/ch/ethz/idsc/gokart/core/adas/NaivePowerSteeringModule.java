// code by jph
package ch.ethz.idsc.gokart.core.adas;

public class NaivePowerSteeringModule extends PowerSteeringModule {
  public NaivePowerSteeringModule() {
    super(new NaivePowerSteering(HapticSteerConfig.GLOBAL));
  }
}
