// code by mh, jph
package ch.ethz.idsc.gokart.core.sound;

import java.util.Arrays;
import java.util.List;

import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;

public class GokartSoundLcmModule extends AbstractModule {
  private final List<SoundExciter> soundExciters = Arrays.asList( //
      new ElectricExciter(250, 1, -10, 250, 0.03f, 5, 0.8f), //
      new ElectricExciter(150, 1, -10, 248, 0.03f, 5, 0.8f), //
      new ElectricExciter(270, 1, -10, 246, 0.03f, 5, 0.8f), //
      new ElectricExciter(600, 1, -10, 220, 0.03f, 5, 0.8f), //
      new ElectricExciter(030, 1, -10, 248, 0.03f, 5, 0.8f));
  // ---
  private final List<SoundResonator> soundResonators = Arrays.asList( //
      new SimpleResonator(1300000f, 30f, 100000f), //
      new SimpleResonator(1310000f, 20f, 100000f));
  // ---
  private final GokartMotorStateLcmProvider gokartMotorStateLcmProvider = new GokartMotorStateLcmProvider();
  private final GokartSoundCreator gokartSoundCreator = new GokartSoundCreator(soundExciters, soundResonators, //
      new ChirpSpeedModifier(5, 0.4f), gokartMotorStateLcmProvider);

  @Override // from AbstractModule
  protected void first() throws Exception {
    gokartMotorStateLcmProvider.start();
    gokartSoundCreator.start();
  }

  @Override // from AbstractModule
  protected void last() {
    gokartSoundCreator.stop();
    gokartMotorStateLcmProvider.stop();
  }

  public static void main(String[] args) {
    ModuleAuto.INSTANCE.runOne(GokartSoundLcmModule.class);
  }
}
