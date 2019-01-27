// code by mh
package ch.ethz.idsc.gokart.core.sound;

public class SimpleResonator implements SoundResonator {
  private final float spring;
  private final float damping;
  private final float excitability;
  // ---
  private float x = 0;
  private float dx = 0;

  public SimpleResonator(float spring, float damping, float excitability) {
    this.spring = spring;
    this.damping = damping;
    this.excitability = excitability;
  }

  @Override
  public float getNextValue(float excitementValue, GokartSoundState state, float dt) {
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
