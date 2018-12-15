// code by mh
package ch.ethz.idsc.gokart.core.sound;

import ch.ethz.idsc.gokart.core.sound.GokartSoundCreator.MotorState;

public class SimpleResonator extends GokartSoundCreator.Resonator {
  float x = 0;
  float dx = 0;
  final float spring;
  final float damping;
  final float excitability;

  public SimpleResonator(float spring, float damping, float excitability) {
    this.spring = spring;
    this.damping = damping;
    this.excitability = excitability;
  }

  @Override
  public float getNextValue(float excitementValue, MotorState state, float dt) {
    // if(excitementValue>0)
    // System.out.println("ping");
    float ddx = excitementValue * excitability - dx * damping - x * spring;
    dx += ddx * dt;
    x += dx * dt;
    if (x > 1) {
      x = 1;
      if (dx > 0)
        dx = 0;
    }
    if (x < -1) {
      x = -1;
      if (dx < 0)
        dx = 0;
    }
    return x;
  }
}
