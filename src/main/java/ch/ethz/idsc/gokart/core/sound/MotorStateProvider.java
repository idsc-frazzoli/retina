// code by mh
package ch.ethz.idsc.gokart.core.sound;

@FunctionalInterface
public interface MotorStateProvider {
  GokartSoundState getMotorState(float time);
}
