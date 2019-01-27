// code by mh
package ch.ethz.idsc.gokart.core.sound;

import java.io.File;
import java.util.Optional;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

import ch.ethz.idsc.gokart.core.fuse.EmergencyBrakeProvider;
import ch.ethz.idsc.gokart.core.man.ManualConfig;
import ch.ethz.idsc.gokart.dev.linmot.LinmotSocket;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnTracker;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.joystick.ManualControlProvider;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractClockedModule;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.Timing;
import ch.ethz.idsc.tensor.qty.Quantity;

public class GokartVoiceOutputs extends AbstractClockedModule {
  private final ManualControlProvider manualControlProvider = ManualConfig.GLOBAL.createProvider();
  private final SteerColumnTracker steerColumnTracker = SteerSocket.INSTANCE.getSteerColumnTracker();
  // ---
  private boolean calibrationSaid = false;
  private boolean emergencyBrakingSaid = false;
  private Timing timeSinceEmergenyCallout = Timing.started();
  private double durationBetweenEmergenyCallouts = 3;
  private Timing timeSinceDriverCallout = Timing.started();
  private double durationBetweenDriverCallouts = 3;
  private boolean humanDrivingSaid = true;

  private static void playFile(File file) {
    try {
      AudioInputStream stream = AudioSystem.getAudioInputStream(file);
      AudioFormat format = stream.getFormat();
      DataLine.Info info = new DataLine.Info(Clip.class, format);
      Clip clip = (Clip) AudioSystem.getLine(info);
      clip.open(stream);
      clip.start();
    } catch (Exception exception) {
      exception.printStackTrace();
      System.err.println("Not possible to play sound!");
    }
  }

  private static void sayCalibrated() {
    // TODO JPH/MH convert to mp3 and put file in ephemeral
    File file = HomeDirectory.file("Documents/CalibrationSignal.wav");
    playFile(file);
  }

  private static void sayEmergenyBraking() {
    // TODO: put it in resources
    File file = HomeDirectory.file("Documents/ObstacleDetectedWarning.wav");
    playFile(file);
  }

  private static void sayHumanDriving() {
    File file = HomeDirectory.file("Documents/humanSignal.wav");
    playFile(file);
  }

  private static void sayAIDriving() {
    File file = HomeDirectory.file("Documents/AISignal.wav");
    playFile(file);
  }

  @Override
  protected void first() throws Exception {
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
    if (!calibrationSaid && steerColumnTracker.isCalibratedAndHealthy()) {
      sayCalibrated();
      calibrationSaid = true;
    }
    if (!emergencyBrakingSaid && LinmotSocket.INSTANCE.getClass().equals(EmergencyBrakeProvider.class)) {
      sayEmergenyBraking();
      timeSinceEmergenyCallout = Timing.started();
    }
    if (emergencyBrakingSaid && //
        timeSinceEmergenyCallout.seconds() > durationBetweenEmergenyCallouts && //
        !LinmotSocket.INSTANCE.getClass().equals(EmergencyBrakeProvider.class))
      emergencyBrakingSaid = false;
    boolean humanDriving = !isAutonomousPressed();
    if (humanDriving && !humanDrivingSaid) {
      humanDrivingSaid = humanDriving;
      if (timeSinceDriverCallout.seconds() > durationBetweenDriverCallouts) {
        sayHumanDriving();
        timeSinceDriverCallout = Timing.started();
      }
    } else if (!humanDriving && humanDrivingSaid) {
      humanDrivingSaid = humanDriving;
      if (timeSinceDriverCallout.seconds() > durationBetweenDriverCallouts) {
        sayAIDriving();
        timeSinceDriverCallout = Timing.started();
      }
    }
  }

  @Override
  protected Scalar getPeriod() {
    return Quantity.of(0.2, SI.SECOND);
  }
}
