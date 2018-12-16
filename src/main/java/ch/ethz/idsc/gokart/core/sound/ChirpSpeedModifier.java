// code by mh
package ch.ethz.idsc.gokart.core.sound;

import ch.ethz.idsc.gokart.core.sound.GokartSoundCreator.MotorState;

public class ChirpSpeedModifier implements GokartSoundCreator.SpeedModifier {
  private final float overSpeedRate;
  private final float overSpeedFactor;
  // ---
  private float overSpeedFloat = 0;

  public ChirpSpeedModifier(float overSpeedRate, float overSpeedFactor) {
    this.overSpeedRate = overSpeedRate;
    this.overSpeedFactor = overSpeedFactor;
  }

  @Override
  public float getNextSpeedValue(MotorState motorState, float dt) {
    overSpeedFloat += overSpeedRate * dt;
    if (overSpeedFloat > 1)
      overSpeedFloat = 0;
    return motorState.speed + overSpeedFloat * overSpeedFactor * motorState.torquevectoring;
  }
}
