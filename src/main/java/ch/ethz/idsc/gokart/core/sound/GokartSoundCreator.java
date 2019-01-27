// code by mh
package ch.ethz.idsc.gokart.core.sound;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.tensor.io.Timing;

public class GokartSoundCreator implements StartAndStoppable, Runnable {
  private static final int SAMPLING_RATE = 44100;
  private static final float DT = 1f / SAMPLING_RATE;
  private static final int SAMPLE_SIZE = 2;
  private static final float MAGIC = 1.0f * Short.MAX_VALUE;
  // ---
  private AudioFormat audioFormat;
  private SourceDataLine sourceDataLine;
  private DataLine.Info info;
  private ByteBuffer byteBuffer;
  private Timing timing = Timing.stopped();
  private GokartSoundState gokartSoundState = new GokartSoundState(0, 0, 0);
  private final List<SoundExciter> exciters;
  private final List<SoundResonator> resonators;
  private final SpeedModifier speedModifier;
  private MotorStateProvider motorStateProvider;
  private boolean isLaunched = true;
  private Thread thread;

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

  @Override // from StartAndStoppable
  public void start() {
    audioFormat = new AudioFormat(SAMPLING_RATE, SAMPLE_SIZE * 8, 1, true, true);
    info = new DataLine.Info(SourceDataLine.class, audioFormat);
    if (!AudioSystem.isLineSupported(info)) {
      System.out.println("Line matching " + info + " is not supported.");
      throw new RuntimeException();
    }
    try {
      sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);
      sourceDataLine.open(audioFormat);
      sourceDataLine.start();
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    byteBuffer = ByteBuffer.allocate(sourceDataLine.getBufferSize());
    timing.start();
    Thread thread = new Thread(this);
    thread.start();
  }

  @Override // from Runnable
  public void run() {
    while (isLaunched) {
      gokartSoundState = motorStateProvider.getMotorState((float) timing.seconds());
      fillBuffer((int) (0.97f * sourceDataLine.available() / SAMPLE_SIZE));
      while (sourceDataLine.getBufferSize() * 0.98 < sourceDataLine.available())
        try {
          Thread.sleep(5);
        } catch (Exception exception) {
          // ---
        }
    }
  }

  public void setState(GokartSoundState motorState) {
    this.gokartSoundState = motorState;
  }

  private void fillBuffer(int samples) {
    GokartSoundState state = this.gokartSoundState;
    byteBuffer.clear();
    for (int i = 0; i < samples; ++i) {
      // FIXME not consistent logic
      float newSpeed = speedModifier.getNextSpeedValue(gokartSoundState, DT);
      state = new GokartSoundState(newSpeed, state.power, state.torqueVectoring);
      float excitementValue = 0;
      for (SoundExciter exciter : exciters)
        excitementValue += exciter.getNextValue(state, DT);
      // System.out.println("exc: "+excitementValue);
      float value = 0f;
      for (SoundResonator resonator : resonators)
        value += resonator.getNextValue(excitementValue, state, DT);
      value += excitementValue;
      byteBuffer.putShort((short) (MAGIC * value));
    }
    sourceDataLine.write(byteBuffer.array(), 0, byteBuffer.position());
  }

  @Override // from StartAndStoppable
  public void stop() {
    isLaunched = false;
    if (Objects.nonNull(thread))
      thread.interrupt();
    sourceDataLine.drain();
    sourceDataLine.close();
  }
}
