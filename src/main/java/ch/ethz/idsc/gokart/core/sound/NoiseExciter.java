// code by mh
package ch.ethz.idsc.gokart.core.sound;

import java.util.Random;

/* package */ class NoiseExciter implements SoundExciter {
  private final Random random = new Random();
  private final float amplitude;

  public NoiseExciter(float amplitude) {
    this.amplitude = amplitude;
  }

  @Override
  public float getNextValue(GokartSoundState state, float dt) {
    return amplitude * (random.nextFloat() - 0.5f) * state.power;
  }
}
