// code by jph
package ch.ethz.idsc.gokart.core.sound;

import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetListener;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutListener;
import ch.ethz.idsc.gokart.lcm.autobox.RimoGetLcmClient;
import ch.ethz.idsc.gokart.lcm.autobox.RimoPutLcmClient;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.tensor.red.Mean;

/* package */ class GokartMotorStateLcmProvider implements StartAndStoppable, MotorStateProvider, RimoGetListener, RimoPutListener {
  private static final float MULTI = (float) (1.0 / (2 * 2315));
  private final RimoGetLcmClient rimoGetLcmClient = new RimoGetLcmClient();
  private final RimoPutLcmClient rimoPutLcmClient = new RimoPutLcmClient();
  // ---
  private RimoGetEvent rimoGetEvent = RimoGetEvents.create(0, 0);
  private RimoPutEvent rimoPutEvent = RimoPutEvent.PASSIVE;

  public GokartMotorStateLcmProvider() {
    rimoGetLcmClient.addListener(this);
    rimoPutLcmClient.addListener(this);
  }

  @Override // from StartAndStoppable
  public void start() {
    rimoGetLcmClient.startSubscriptions();
    rimoPutLcmClient.startSubscriptions();
  }

  @Override // from StartAndStoppable
  public void stop() {
    rimoGetLcmClient.stopSubscriptions();
    rimoPutLcmClient.stopSubscriptions();
  }

  @Override // from MotorStateProvider
  public GokartSoundState getMotorState(float time) {
    float speed = Mean.of(rimoGetEvent.getAngularRate_Y_pair()).Get().abs().number().floatValue() * 0.05f;
    short sL = rimoPutEvent.putTireL.getTorqueRaw();
    short sR = rimoPutEvent.putTireR.getTorqueRaw();
    sL = (short) (sL < 0 ? -sL : sL);
    sR = (short) (sR < 0 ? -sR : sR);
    float power = (sL + sR) * MULTI;
    float tv = (sR - sL) * MULTI;
    return new GokartSoundState(speed, power, tv);
  }

  @Override // from RimoGetListener
  public void getEvent(RimoGetEvent rimoGetEvent) {
    this.rimoGetEvent = rimoGetEvent;
  }

  @Override // from RimoPutListener
  public void putEvent(RimoPutEvent putEvent) {
    this.rimoPutEvent = putEvent;
  }
}