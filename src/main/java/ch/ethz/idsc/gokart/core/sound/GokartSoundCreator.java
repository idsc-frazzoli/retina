// code by mh
package ch.ethz.idsc.gokart.core.sound;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import ch.ethz.idsc.owl.data.Stopwatch;

public class GokartSoundCreator {
  private static final int SAMPLING_RATE = 44100;
  private static final float DT = 1f / SAMPLING_RATE;
  private static final int SAMPLE_SIZE = 2;
  // ---
  private AudioFormat audioFormat;
  private SourceDataLine sourceDataLine;
  private DataLine.Info info;
  private ByteBuffer byteBuffer;
  private Stopwatch started = Stopwatch.stopped();
  private GokartSoundState motorState = new GokartSoundState(0, 0, 0);
  private final List<SoundExciter> exciters;
  private final List<SoundResonator> resonators;
  private final SpeedModifier speedModifier;
  private MotorStateProvider motorStateProvider;

  public GokartSoundCreator( //
      List<SoundExciter> exciters, //
      List<SoundResonator> resonators, //
      SpeedModifier speedModifier, //
      MotorStateProvider motorStateProvider) {
    this.exciters = Objects.requireNonNull(exciters);
    this.resonators = Objects.requireNonNull(resonators);
    this.motorStateProvider = Objects.requireNonNull(motorStateProvider);
    this.speedModifier = Objects.requireNonNull(speedModifier);
  }

  public int getSamplingRate() {
    return SAMPLING_RATE;
  }

  public void playSimple(float seconds) throws InterruptedException, LineUnavailableException {
    first();
    started.start();
    while (started.display_seconds() < seconds) {
      motorState = motorStateProvider.getMotorState((float) started.display_seconds());
      // System.out.println(sourceDataLine.available());
      // System.out.println(sourceDataLine.getBufferSize());
      fillBuffer((int) (0.97f * sourceDataLine.available() / SAMPLE_SIZE));
      while (sourceDataLine.getBufferSize() * 0.8 < sourceDataLine.available())
        Thread.sleep(5);
    }
    last();
  }

  void first() throws LineUnavailableException {
    audioFormat = new AudioFormat(SAMPLING_RATE, SAMPLE_SIZE * 8, 1, true, true);
    info = new DataLine.Info(SourceDataLine.class, audioFormat);
    if (!AudioSystem.isLineSupported(info)) {
      System.out.println("Line matching " + info + " is not supported.");
      throw new LineUnavailableException();
    }
    sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
    sourceDataLine.open(audioFormat);
    sourceDataLine.start();
    byteBuffer = ByteBuffer.allocate(sourceDataLine.getBufferSize());
  }

  void last() {
    sourceDataLine.drain();
    sourceDataLine.close();
  }

  public void setState(GokartSoundState motorState) {
    this.motorState = motorState;
  }

  public void fillBuffer(int samples) {
    // System.out.println(samples);
    GokartSoundState state = this.motorState;
    byteBuffer.clear();
    for (int i = 0; i < samples; ++i) {
      // FIXME not consistent logic
      float newSpeed = speedModifier.getNextSpeedValue(motorState, DT);
      state = new GokartSoundState(newSpeed, state.power, state.torquevectoring);
      float excitementValue = 0;
      for (SoundExciter exciter : exciters)
        excitementValue += exciter.getNextValue(state, DT);
      // System.out.println("exc: "+excitementValue);
      float value = 0f;
      for (SoundResonator resonator : resonators)
        value += resonator.getNextValue(excitementValue, state, DT);
      value += excitementValue;
      byteBuffer.putShort((short) (Short.MAX_VALUE * value));
    }
    sourceDataLine.write(byteBuffer.array(), 0, byteBuffer.position());
  }
}
