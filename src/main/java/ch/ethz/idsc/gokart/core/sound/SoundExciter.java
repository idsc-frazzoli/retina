// code by mh
package ch.ethz.idsc.gokart.core.sound;

/** implementations are mutable */
@FunctionalInterface
public interface SoundExciter {
  /** @param gokartSoundState
   * @param dt
   * @return */
  float getNextValue(GokartSoundState gokartSoundState, float dt);
}