package ch.ethz.idsc.gokart.core.sound;

import java.nio.ByteBuffer;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import ch.ethz.idsc.owl.data.Stopwatch;

public class GokartSoundCreator {
  public static class MotorState {
    public MotorState(float speed, float power, float torquevectoring) {
      this.speed = speed;
      this.power = power;
      // System.out.println("sp: "+speed+"power: "+power);
      this.torquevectoring = torquevectoring;
    }

    public final float speed;
    public final float power;
    public final float torquevectoring;
  }

  public static abstract class Exciter {
    public abstract float getNextValue(MotorState state, float dt);
  }

  public static abstract class Resonator {
    public abstract float getNextValue(float excitementValue, MotorState state, float dt);
  }

  public static interface SpeedModifier {
    float getNextSpeedValue(MotorState defaultState, float dt);
  }

  AudioInputStream audioInputStream;
  AudioFormat audioFormat;
  protected final int SAMPLING_RATE = 44100;
  final int SAMPLE_SIZE = 2;
  final int FRAME_SIZE = 2;
  SourceDataLine sourceDataLine;
  DataLine.Info info;
  ByteBuffer cBuf;
  Stopwatch started = Stopwatch.stopped();
  float speed = 10000;
  float sinPosition = 0;
  boolean written = false;
  MotorState motorState = new MotorState(0, 0, 0);
  final List<Exciter> exciters;
  final List<Resonator> resonators;
  final SpeedModifier speedModifier;
  MotorStateProvider motorStateProvider;

  public GokartSoundCreator(List<Exciter> exciters, List<Resonator> resonators, SpeedModifier speedModifier, MotorStateProvider provider) {
    this.exciters = exciters;
    this.resonators = resonators;
    this.motorStateProvider = provider;
    this.speedModifier = speedModifier;
  }

  public int getSamplingRate() {
    return SAMPLING_RATE;
  }

  public void playSimple(float seconds) throws InterruptedException, LineUnavailableException {
    first();
    started.start();
    while (started.display_seconds() < seconds) {
      motorState = motorStateProvider.getMotorState((float) started.display_seconds());
      fillBuffer((int) (0.97 * sourceDataLine.available() / SAMPLE_SIZE));
      while (sourceDataLine.getBufferSize() * 0.8 < sourceDataLine.available())
        Thread.sleep(1);
    }
    last();
  }

  void first() throws LineUnavailableException {
    audioFormat = new AudioFormat(SAMPLING_RATE, 16, 1, true, true);
    // audioFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, SAMPLING_RATE, 16, 1, FRAME_SIZE, SAMPLING_RATE, true);
    info = new DataLine.Info(SourceDataLine.class, audioFormat);
    if (!AudioSystem.isLineSupported(info)) {
      System.out.println("Line matching " + info + " is not supported.");
      throw new LineUnavailableException();
    }
    sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
    sourceDataLine.open(audioFormat);
    sourceDataLine.start();
    cBuf = ByteBuffer.allocate(sourceDataLine.getBufferSize());
  }

  void last() {
    sourceDataLine.drain();
    sourceDataLine.close();
  }

  public void setState(MotorState motorState) {
    this.motorState = motorState;
  }

  public void fillBuffer(int samples) {
    MotorState state = this.motorState;
    cBuf.clear();
    for (int i = 0; i < samples; i++) {
      if (speedModifier != null) {
        float newSpeed = speedModifier.getNextSpeedValue(motorState, 1.f / SAMPLING_RATE);
        state = new MotorState(newSpeed, state.power, state.torquevectoring);
      }
      float excitementValue = 0;
      for (Exciter e : exciters) {
        excitementValue += e.getNextValue(state, 1.f / SAMPLING_RATE);
      }
      // System.out.println("exc: "+excitementValue);
      short value = 0;
      if (resonators != null) {
        for (Resonator r : resonators) {
          value += Short.MAX_VALUE * r.getNextValue(excitementValue, state, 1.f / SAMPLING_RATE);
        }
      }
      // value = 10000;
      value += (Short.MAX_VALUE * excitementValue);
      cBuf.putShort(value);
    }
    sourceDataLine.write(cBuf.array(), 0, cBuf.position());
  }
}
