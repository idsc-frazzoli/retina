package ch.ethz.idsc.demo.mh;

import ch.ethz.idsc.gokart.core.sound.GokartSoundCreator;
import ch.ethz.idsc.gokart.core.sound.GokartSoundCreator.MotorState;
import ch.ethz.idsc.gokart.core.sound.MotorStateProvider;

public class MotorStateFaker implements MotorStateProvider {
  float t;

  GokartSoundCreator.MotorState getNextMotorState(float dt) {
    t += dt;
    return getNextMotorStateAt(t);
  }

  GokartSoundCreator.MotorState getNextMotorStateAt(float t) {
    this.t = t;
    if (t < 1) {
      return new GokartSoundCreator.MotorState(0, 0, 0);
    } else if (t < 6) {
      float lt = t - 1;
      return new GokartSoundCreator.MotorState(lt * 2f, 0.4f + 0.6f * (lt / 5), 0);
    } else if (t < 8) {
      return new GokartSoundCreator.MotorState(10.0f, 1f, 1);
    } else {
      float lt = t - 8;
      return new GokartSoundCreator.MotorState(10f - lt, 0f, 0);
    }
  }

  @Override
  public MotorState getMotorState(float time) {
    return getNextMotorStateAt(time);
  }
}
