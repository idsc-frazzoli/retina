package ch.ethz.idsc.gokart.core.sound;

import java.io.File;
import java.util.Optional;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

import ch.ethz.idsc.gokart.core.fuse.EmergencyBrakeProvider;
import ch.ethz.idsc.gokart.core.joy.ManualConfig;
import ch.ethz.idsc.gokart.dev.linmot.LinmotSocket;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnTracker;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.owl.data.Stopwatch;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.joystick.ManualControlProvider;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractClockedModule;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.qty.Quantity;

public class GokartVoiceOutputs extends AbstractClockedModule {
  private final ManualControlProvider manualControlProvider = ManualConfig.GLOBAL.createProvider();
  boolean calibrationSaid = false;
  SteerColumnTracker columnTracker;
  boolean emergencyBrakingSaid = false;
  Stopwatch timeSinceEmergenyCallout = Stopwatch.started();
  double durationBetweenEmergenyCallouts = 3;
  Stopwatch timeSinceDriverCallout = Stopwatch.started();
  double durationBetweenDriverCallouts = 3;
  boolean HumanDrivingSaid = true;

  void playFile(File file) {
    try {
      AudioInputStream stream;
      AudioFormat format;
      DataLine.Info info;
      Clip clip;
      stream = AudioSystem.getAudioInputStream(file);
      format = stream.getFormat();
      info = new DataLine.Info(Clip.class, format);
      clip = (Clip) AudioSystem.getLine(info);
      clip.open(stream);
      clip.start();
    } catch (Exception e) {
      System.err.println("Not possible to play sound!");
    }
  }

  void sayCalibrated() {
    // TODO: put it in resources
    File file = HomeDirectory.file("Documents/CalibrationSignal.wav");
    playFile(file);
  }

  void sayEmergenyBraking() {
    // TODO: put it in resources
    File file = HomeDirectory.file("Documents/ObstacleDetectedWarning.wav");
    playFile(file);
  }

  void sayHumanDriving() {
    File file = HomeDirectory.file("Documents/humanSignal.wav");
    playFile(file);
  }

  void sayAIDriving() {
    File file = HomeDirectory.file("Documents/AISignal.wav");
    playFile(file);
  }

  @Override
  protected void first() throws Exception {
    columnTracker = SteerSocket.INSTANCE.getSteerColumnTracker();
    manualControlProvider.start();
  }

  private boolean isAutonomousPressed() {
    // TODO: remove duplicated code
    Optional<ManualControlInterface> optional = manualControlProvider.getManualControl();
    if (optional.isPresent()) { // is joystick button "autonomous" pressed?
      ManualControlInterface gokartJoystickInterface = optional.get();
      return gokartJoystickInterface.isAutonomousPressed();
    }
    return false;
  }

  @Override
  protected void last() {
    manualControlProvider.stop();
  }

  @Override
  protected void runAlgo() {
    if (!calibrationSaid && columnTracker.isCalibratedAndHealthy()) {
      sayCalibrated();
      calibrationSaid = true;
    }
    if (!emergencyBrakingSaid && LinmotSocket.INSTANCE.getClass().equals(EmergencyBrakeProvider.class)) {
      sayEmergenyBraking();
      timeSinceEmergenyCallout = Stopwatch.started();
    }
    if (emergencyBrakingSaid && //
        timeSinceEmergenyCallout.display_seconds() > durationBetweenEmergenyCallouts && //
        !LinmotSocket.INSTANCE.getClass().equals(EmergencyBrakeProvider.class))
      emergencyBrakingSaid = false;
    boolean humanDriving = !isAutonomousPressed();
    if (humanDriving && !HumanDrivingSaid) {
      HumanDrivingSaid = humanDriving;
      if (timeSinceDriverCallout.display_seconds() > durationBetweenDriverCallouts) {
        sayHumanDriving();
        timeSinceDriverCallout = Stopwatch.started();
      }
    } else if (!humanDriving && HumanDrivingSaid) {
      HumanDrivingSaid = humanDriving;
      if (timeSinceDriverCallout.display_seconds() > durationBetweenDriverCallouts) {
        sayAIDriving();
        timeSinceDriverCallout = Stopwatch.started();
      }
    }
  }

  @Override
  protected Scalar getPeriod() {
    return Quantity.of(0.1, SI.SECOND);
  }
}
