// code by mh
package ch.ethz.idsc.gokart.core.sound;

public interface SoundResonator {
  public abstract float getNextValue(float excitementValue, GokartSoundState state, float dt);
}