// code by jph
package ch.ethz.idsc.demo.mh;

import ch.ethz.idsc.gokart.core.sound.GokartSoundState;
import ch.ethz.idsc.gokart.core.sound.MotorStateProvider;

/* package */ class MotorStateFaker implements MotorStateProvider {
  private float t;

  GokartSoundState getNextMotorState(float dt) {
    t += dt;
    return getNextMotorStateAt(t);
  }

  GokartSoundState getNextMotorStateAt(float t) {
    this.t = t;
    if (t < 1)
      return new GokartSoundState(0, 0, 0);
    else //
    if (t < 6) {
      float lt = t - 1;
      return new GokartSoundState(lt * 2f, 0.4f + 0.6f * (lt / 5), 0);
    } else //
    if (t < 8)
      return new GokartSoundState(10.0f, 1f, 1);
    else {
      float lt = t - 8;
      return new GokartSoundState(10f - lt, 0f, 0);
    }
  }

  @Override
  public GokartSoundState getMotorState(float time) {
    return getNextMotorStateAt(time);
  }
}
