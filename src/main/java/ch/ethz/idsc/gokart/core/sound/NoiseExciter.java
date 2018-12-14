package ch.ethz.idsc.gokart.core.sound;

import java.util.Random;

import ch.ethz.idsc.gokart.core.sound.GokartSoundCreator.Exciter;
import ch.ethz.idsc.gokart.core.sound.GokartSoundCreator.MotorState;

public class NoiseExciter extends Exciter {
  final float amplitude;
  Random rnd;
  public NoiseExciter(float amplitude) {
    this.amplitude = amplitude;
    rnd = new Random();
  }

  @Override
  public float getNextValue(MotorState state, float dt) {
    return amplitude*(rnd.nextFloat()-0.5f)*state.power;
  }
}
