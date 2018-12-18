// code by mh
package ch.ethz.idsc.gokart.core.sound;

/** implementations are mutable */
public interface SoundExciter {
  float getNextValue(GokartSoundState motorState, float dt);
}