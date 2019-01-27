// code by mh
package ch.ethz.idsc.gokart.core.sound;

@FunctionalInterface
public interface SoundResonator {
  float getNextValue(float excitementValue, GokartSoundState gokartSoundState, float dt);
}