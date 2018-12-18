// code by mh
package ch.ethz.idsc.gokart.core.sound;

public interface SpeedModifier {
  float getNextSpeedValue(GokartSoundState defaultState, float dt);
}