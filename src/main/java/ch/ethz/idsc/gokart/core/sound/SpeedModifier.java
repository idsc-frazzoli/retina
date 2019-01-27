// code by mh
package ch.ethz.idsc.gokart.core.sound;

@FunctionalInterface
/* package */ interface SpeedModifier {
  float getNextSpeedValue(GokartSoundState defaultState, float dt);
}