package ch.ethz.idsc.gokart.core.sound;

import ch.ethz.idsc.gokart.core.sound.GokartSoundCreator.MotorState;

public class ChirpSpeedModifier implements GokartSoundCreator.SpeedModifier {
  final float overSpeedRate;
  final float overSpeedFactor;
  float overSpeedFloat = 0;

  public ChirpSpeedModifier(float overSpeedRate, float overSpeedFactor) {
    this.overSpeedRate = overSpeedRate;
    this.overSpeedFactor = overSpeedFactor;
  }

  @Override
  public float getNextSpeedValue(MotorState defaultState, float dt) {
    overSpeedFloat += overSpeedRate * dt;
    if (overSpeedFloat > 1)
      overSpeedFloat = 0;
    return defaultState.speed + overSpeedFloat * overSpeedFactor * defaultState.torquevectoring;
  }
}
