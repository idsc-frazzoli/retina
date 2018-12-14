package ch.ethz.idsc.gokart.core.sound;

public interface MotorStateProvider {
  GokartSoundCreator.MotorState getMotorState(float time);
}
