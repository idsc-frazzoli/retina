package ch.ethz.idsc.gokart.core.sound;

import java.util.ArrayList;

import ch.ethz.idsc.demo.mh.MotorStateFaker;
import ch.ethz.idsc.gokart.core.joy.JoystickConfig;
import ch.ethz.idsc.gokart.core.sound.GokartSoundCreator.MotorState;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetListener;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.sca.Abs;

public class SoundModule extends AbstractModule implements RimoGetListener {
  Scalar meanTangentSpeed;
  Scalar power;
  Scalar torqueVectoring;

  @Override
  public void getEvent(RimoGetEvent getEvent) {
    meanTangentSpeed = ChassisGeometry.GLOBAL.odometryTangentSpeed(getEvent);
    Scalar leftPower = getEvent.getTireL.getRmsMotorCurrent().divide(JoystickConfig.GLOBAL.torqueLimit);
    Scalar rightPower = getEvent.getTireR.getRmsMotorCurrent().divide(JoystickConfig.GLOBAL.torqueLimit);
    power = (Scalar) Mean.of(Tensors.of(leftPower, rightPower));
    torqueVectoring = Abs.of(leftPower.subtract(rightPower)).divide(RealScalar.of(2));
  }

  @Override
  protected void first() throws Exception {
    ElectricExciter exciter3 = new ElectricExciter(//
        250, 1, -10, 250, 0.03f, 5, 0.8f);
    ElectricExciter exciter4 = new ElectricExciter(//
        150, 1, -10, 248, 0.03f, 5, 0.8f);
    ElectricExciter exciter5 = new ElectricExciter(//
        270, 1, -10, 246, 0.03f, 5, 0.8f);
    ElectricExciter exciter6 = new ElectricExciter(//
        600, 1, -10, 220, 0.03f, 5, 0.8f);
    ElectricExciter exciter7 = new ElectricExciter(//
        30, 1, -10, 248, 0.03f, 5, 0.8f);
    // NoiseExciter exciter8 = new NoiseExciter(0.04f);
    ArrayList<GokartSoundCreator.Exciter> exciters = new ArrayList<>();
    // exciters.add(exciter1);
    // exciters.add(exciter2);
    exciters.add(exciter3);
    exciters.add(exciter4);
    exciters.add(exciter5);
    exciters.add(exciter6);
    exciters.add(exciter7);
    // exciters.add(exciter8);
    // GokartSoundCreator.Exciter exciter = new TestExciter(30000, 0,10);
    GokartSoundCreator.Resonator resonator1 = new SimpleResonator(1300000f, 30f, 100000f);
    GokartSoundCreator.Resonator resonator2 = new SimpleResonator(1310000f, 20f, 100000f);
    // GokartSoundCreator.Resonator resonator3 = new SimpleResonator(10010000f, 10f, 20000f);
    ArrayList<GokartSoundCreator.Resonator> resonators = new ArrayList<>();
    resonators.add(resonator1);
    resonators.add(resonator2);
    // resonators.add(resonator3);
    MotorStateFaker faker = new MotorStateFaker();
    ChirpSpeedModifier chirping = new ChirpSpeedModifier(5, 0.4f);
    GokartSoundCreator creator = new GokartSoundCreator(exciters, resonators, chirping, faker);
    creator.setState(new MotorState(5, 1f, 0));
    creator.playSimple(10f);
  }

  @Override
  protected void last() {
    // TODO Auto-generated method stub
  }
}
