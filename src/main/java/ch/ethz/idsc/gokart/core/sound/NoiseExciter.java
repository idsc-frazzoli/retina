// code by mh
package ch.ethz.idsc.gokart.core.sound;

import java.util.Random;

import ch.ethz.idsc.gokart.core.sound.GokartSoundCreator.Exciter;
import ch.ethz.idsc.gokart.core.sound.GokartSoundCreator.MotorState;

public class NoiseExciter extends Exciter {
  private final float amplitude;
  private final Random random = new Random();

  public NoiseExciter(float amplitude) {
    this.amplitude = amplitude;
  }

  @Override
  public float getNextValue(MotorState state, float dt) {
    return amplitude * (random.nextFloat() - 0.5f) * state.power;
  }
}
