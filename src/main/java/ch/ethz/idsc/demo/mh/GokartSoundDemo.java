package ch.ethz.idsc.demo.mh;

import java.util.ArrayList;

import javax.sound.sampled.LineUnavailableException;

import ch.ethz.idsc.gokart.core.sound.ChirpSpeedModifier;
import ch.ethz.idsc.gokart.core.sound.ElectricExciter;
import ch.ethz.idsc.gokart.core.sound.GokartSoundCreator;
import ch.ethz.idsc.gokart.core.sound.GokartSoundCreator.MotorState;
import ch.ethz.idsc.gokart.core.sound.NoiseExciter;
import ch.ethz.idsc.gokart.core.sound.SimpleExciter;
import ch.ethz.idsc.gokart.core.sound.SimpleResonator;
import ch.ethz.idsc.gokart.core.sound.TestExciter;

public class GokartSoundDemo {
  public static void main(String[] args) {
    System.out.println("sound demo!");
    try {
      //GokartSoundCreator.Exciter exciter1 = new SimpleExciter(20, 3000,30);
      //GokartSoundCreator.Exciter exciter2 = new SimpleExciter(10, 3000,30);
      ElectricExciter exciter3 = new ElectricExciter(//
          250, 1,
          -10, 250,
          0.03f, 5, 0.8f);
      ElectricExciter exciter4  = new ElectricExciter(//
          150, 1,
          -10, 248,
          0.03f, 5, 0.8f);
      ElectricExciter exciter5  = new ElectricExciter(//
          270, 1,
          -10, 246,
          0.03f, 5, 0.8f);
      ElectricExciter exciter6  = new ElectricExciter(//
          600, 1,
          -10, 220,
          0.03f, 5, 0.8f);
      ElectricExciter exciter7  = new ElectricExciter(//
          30, 1,
          -10, 248,
          0.03f, 5, 0.8f);
      //NoiseExciter exciter8 = new NoiseExciter(0.04f);
      ArrayList<GokartSoundCreator.Exciter> exciters = new ArrayList<>();
      //exciters.add(exciter1);
      //exciters.add(exciter2);
      exciters.add(exciter3);
      exciters.add(exciter4);
      exciters.add(exciter5);
      exciters.add(exciter6);
      exciters.add(exciter7);
      //exciters.add(exciter8);
      //GokartSoundCreator.Exciter exciter = new TestExciter(30000, 0,10);
      GokartSoundCreator.Resonator resonator1 = new SimpleResonator(1300000f, 30f, 100000f);
      GokartSoundCreator.Resonator resonator2 = new SimpleResonator(1310000f, 20f, 100000f);
      //GokartSoundCreator.Resonator resonator3 = new SimpleResonator(10010000f, 10f, 20000f);
      ArrayList<GokartSoundCreator.Resonator> resonators = new ArrayList<>();
      resonators.add(resonator1);
      resonators.add(resonator2);
      //resonators.add(resonator3);
      MotorStateFaker faker = new MotorStateFaker();
      ChirpSpeedModifier chirping = new ChirpSpeedModifier(5, 0.4f);
      GokartSoundCreator creator = new GokartSoundCreator(exciters, resonators, chirping, faker);
      creator.setState(new MotorState(5,1f, 0));
      creator.playSimple(10f);
    } catch (LineUnavailableException | InterruptedException e) {
      System.out.println("no sound! " + e.getMessage());
    }
    System.out.println("finished");
  }
}
