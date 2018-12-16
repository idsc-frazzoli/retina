// code by mh
package ch.ethz.idsc.gokart.core.sound;

public interface SoundExciter {
  float getNextValue(GokartSoundState motorState, float dt);
}