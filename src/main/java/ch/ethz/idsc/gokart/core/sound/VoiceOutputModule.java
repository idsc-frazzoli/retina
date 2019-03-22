// code by mh, jph
// https://stackoverflow.com/questions/2792977/do-i-need-to-close-an-audio-clip
// https://stackoverflow.com/questions/5529754/java-io-ioexception-mark-reset-not-supported
package ch.ethz.idsc.gokart.core.sound;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import ch.ethz.idsc.gokart.core.fuse.EmergencyBrakeProvider;
import ch.ethz.idsc.gokart.core.mpc.MPCRimoProvider;
import ch.ethz.idsc.gokart.dev.linmot.LinmotSocket;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnTracker;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractClockedModule;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.qty.Quantity;

public class VoiceOutputModule extends AbstractClockedModule {
  private final SteerColumnTracker steerColumnTracker = SteerSocket.INSTANCE.getSteerColumnTracker();
  private final Map<VoiceOutput, Clip> map = new HashMap<>();
  // ---
  private boolean calibrationSaid = false;
  // ---
  private boolean emergencyBrakingSaid = false;
  private Timing timeSinceEmergenyCallout = Timing.started();
  private double durationBetweenEmergenyCallouts = 3;
  // ---
  private Timing timeSinceDriverCallout = Timing.started();
  private double durationBetweenDriverCallouts = 3;
  // ---
  private boolean humanDrivingSaid = true;

  synchronized void say(VoiceOutput voiceOutput) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
    if (map.containsKey(voiceOutput))
      map.get(voiceOutput).close(); // mandatory close otherwise memory leak
    map.put(voiceOutput, StaticHelper.play(AudioSystem.getAudioInputStream( //
        new BufferedInputStream(ResourceData.class.getResourceAsStream(voiceOutput.resource())))));
  }

  @Override // from AbstractModule
  protected void first() {
    // ---
  }

  @Override // from AbstractModule
  protected synchronized void last() {
    map.values().forEach(Clip::close);
  }

  @Override // from AbstractClockedModule
  protected void runAlgo() {
    try {
      if (!calibrationSaid && steerColumnTracker.isCalibratedAndHealthy()) {
        say(VoiceOutput.CalibrationSignal);
        calibrationSaid = true;
      }
      boolean emergencyBraking = LinmotSocket.INSTANCE.getPutProviderDesc().equals(EmergencyBrakeProvider.class.getSimpleName());
      if (!emergencyBrakingSaid && emergencyBraking) {
        say(VoiceOutput.ObstacleDetectedWarning);
        timeSinceEmergenyCallout = Timing.started();
      }
      if (emergencyBrakingSaid && //
          timeSinceEmergenyCallout.seconds() > durationBetweenEmergenyCallouts && //
          !emergencyBraking)
        emergencyBrakingSaid = false;
      boolean humanDriving = !RimoSocket.INSTANCE.getPutProviderDesc().equals(MPCRimoProvider.class.getSimpleName());
      if (humanDriving && !humanDrivingSaid) {
        humanDrivingSaid = humanDriving;
        if (timeSinceDriverCallout.seconds() > durationBetweenDriverCallouts) {
          say(VoiceOutput.HumanSignal);
          timeSinceDriverCallout = Timing.started();
        }
      } else if (!humanDriving && humanDrivingSaid) {
        humanDrivingSaid = humanDriving;
        if (timeSinceDriverCallout.seconds() > durationBetweenDriverCallouts) {
          say(VoiceOutput.AISignal);
          timeSinceDriverCallout = Timing.started();
        }
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  @Override // from AbstractClockedModule
  protected Scalar getPeriod() {
    return Quantity.of(0.2, SI.SECOND);
  }
}
