// code by mh
package ch.ethz.idsc.demo.mh;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.gokart.core.sound.ChirpSpeedModifier;
import ch.ethz.idsc.gokart.core.sound.ElectricExciter;
import ch.ethz.idsc.gokart.core.sound.GokartSoundCreator;
import ch.ethz.idsc.gokart.core.sound.GokartSoundState;
import ch.ethz.idsc.gokart.core.sound.SimpleResonator;
import ch.ethz.idsc.gokart.core.sound.SoundExciter;
import ch.ethz.idsc.gokart.core.sound.SoundResonator;

/* package */ enum GokartSoundDemo {
  ;
  public static void main(String[] args) {
    System.out.println("sound demo!");
    try {
      // GokartSoundCreator.Exciter exciter1 = new SimpleExciter(20, 3000,30);
      // GokartSoundCreator.Exciter exciter2 = new SimpleExciter(10, 3000,30);
      List<SoundExciter> exciters = Arrays.asList( //
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
      List<SoundResonator> resonators = Arrays.asList( //
          new SimpleResonator(1300000f, 30f, 100000f), //
          new SimpleResonator(1310000f, 20f, 100000f));
      MotorStateFaker faker = new MotorStateFaker();
      ChirpSpeedModifier chirping = new ChirpSpeedModifier(5, 0.4f);
      GokartSoundCreator creator = new GokartSoundCreator(exciters, resonators, chirping, faker);
      creator.setState(new GokartSoundState(5, 1f, 0));
      creator.start();
      Thread.sleep(10000);
      creator.stop();
    } catch (Exception exception) {
      System.out.println("no sound! " + exception.getMessage());
    }
    System.out.println("finished");
  }
}
