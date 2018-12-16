// code by mh, jph
package ch.ethz.idsc.demo.jph;

import java.util.Arrays;
import java.util.List;

import javax.sound.sampled.LineUnavailableException;

import ch.ethz.idsc.gokart.core.sound.ChirpSpeedModifier;
import ch.ethz.idsc.gokart.core.sound.ElectricExciter;
import ch.ethz.idsc.gokart.core.sound.GokartSoundCreator;
import ch.ethz.idsc.gokart.core.sound.GokartSoundCreator.MotorState;
import ch.ethz.idsc.gokart.core.sound.MotorStateProvider;
import ch.ethz.idsc.gokart.core.sound.SimpleResonator;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvents;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetListener;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutListener;
import ch.ethz.idsc.gokart.lcm.autobox.RimoGetLcmClient;
import ch.ethz.idsc.gokart.lcm.autobox.RimoPutLcmClient;
import ch.ethz.idsc.tensor.red.Mean;

/* package */ class LcmMotorStateProvider implements MotorStateProvider, RimoGetListener, RimoPutListener {
  private final RimoGetLcmClient rimoGetLcmClient = new RimoGetLcmClient();
  private final RimoPutLcmClient rimoPutLcmClient = new RimoPutLcmClient();
  private RimoGetEvent rimoGetEvent = RimoGetEvents.create(0, 0);
  private RimoPutEvent rimoPutEvent = RimoPutEvent.PASSIVE;

  public LcmMotorStateProvider() {
    rimoGetLcmClient.addListener(this);
    rimoGetLcmClient.startSubscriptions();
    rimoPutLcmClient.addListener(this);
    rimoPutLcmClient.startSubscriptions();
  }

  @Override
  public MotorState getMotorState(float time) {
    float speed = Mean.of(rimoGetEvent.getAngularRate_Y_pair()).Get().abs().number().floatValue() * 0.05f;
    short sL = rimoPutEvent.putTireL.getTorqueRaw();
    short sR = rimoPutEvent.putTireR.getTorqueRaw();
    sL = (short) (sL < 0 ? -sL : sL);
    sR = (short) (sR < 0 ? -sR : sR);
    float power = (sL + sR) * 1e-4f;
    // System.out.println(speed + " " + power);
    return new MotorState(speed, power, .2f);
  }

  @Override
  public void getEvent(RimoGetEvent rimoGetEvent) {
    this.rimoGetEvent = rimoGetEvent;
  }

  @Override
  public void putEvent(RimoPutEvent putEvent) {
    this.rimoPutEvent = putEvent;
  }
}

public enum GokartSoundLcmListener {
  ;
  public static void main(String[] args) {
    System.out.println("sound demo!");
    try {
      // GokartSoundCreator.Exciter exciter1 = new SimpleExciter(20, 3000,30);
      // GokartSoundCreator.Exciter exciter2 = new SimpleExciter(10, 3000,30);
      List<GokartSoundCreator.Exciter> exciters = Arrays.asList( //
          // new TestExciter(440f, 220f, 1), //
          new ElectricExciter(250, 1, -10, 250, 0.03f, 5, 0.8f), //
          new ElectricExciter(150, 1, -10, 248, 0.03f, 5, 0.8f), //
          new ElectricExciter(270, 1, -10, 246, 0.03f, 5, 0.8f), //
          new ElectricExciter(600, 1, -10, 220, 0.03f, 5, 0.8f), //
          new ElectricExciter(030, 1, -10, 248, 0.03f, 5, 0.8f));
      // NoiseExciter exciter8 = new NoiseExciter(0.04f);
      // exciters.add(exciter8);
      // GokartSoundCreator.Exciter exciter = new TestExciter(30000, 0,10);
      // GokartSoundCreator.Resonator resonator3 = new SimpleResonator(10010000f, 10f, 20000f);
      List<GokartSoundCreator.Resonator> resonators = Arrays.asList( //
          new SimpleResonator(1300000f, 30f, 100000f), //
          new SimpleResonator(1310000f, 20f, 100000f));
      LcmMotorStateProvider faker = new LcmMotorStateProvider();
      ChirpSpeedModifier chirping = new ChirpSpeedModifier(5, 0.4f);
      GokartSoundCreator creator = new GokartSoundCreator(exciters, resonators, chirping, faker);
      creator.setState(new MotorState(5, 1f, 0));
      creator.playSimple(100f);
    } catch (LineUnavailableException | InterruptedException e) {
      System.out.println("no sound! " + e.getMessage());
    }
    System.out.println("finished");
  }
}
