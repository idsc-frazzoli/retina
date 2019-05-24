// code by mh
package ch.ethz.idsc.gokart.core.man;

import java.awt.GridLayout;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.dev.rimo.RimoConfig;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetListener;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutHelper;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutTires;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnInterface;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Scalars;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Differences;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.qty.Quantity;

public class AutomaticPowerTestModule extends GuideManualModule<RimoPutEvent> implements RimoGetListener {
  private final Scalar maxSpeed = RimoConfig.GLOBAL.testMaxSpeed;
  private final Scalar minSpeed = Quantity.of(0, SI.VELOCITY);
  private final Scalar speedMargin = Quantity.of(1, SI.VELOCITY);
  private Tensor motorCurrentValues;
  private Tensor bottomUpMaxSpeed;
  private Tensor topDownMinSpeed;
  private Tensor completionIndex;
  private Scalar maxPower;
  private Scalar minPower;
  private Boolean slowDownTriggered = false;
  private Boolean slowDownCompleted = false;
  private int steps = 20;
  private int currentInd = 0;
  private boolean up = true;
  private Scalar meanTangentSpeed = Quantity.of(0, SI.VELOCITY);
  private final JFrame jFrame = new JFrame();
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());
  private JTextArea textarea;

  @Override // from AbstractModule
  void protected_first() {
    RimoSocket.INSTANCE.addPutProvider(this);
    RimoSocket.INSTANCE.addGetListener(this);
    maxPower = Quantity.of(RimoPutTires.MAX_TORQUE, NonSI.ARMS);
    minPower = Quantity.of(RimoPutTires.MIN_TORQUE, NonSI.ARMS);
    motorCurrentValues = Subdivide.of(minPower, maxPower, steps).unmodifiable();
    bottomUpMaxSpeed = Tensors.vector(i -> minSpeed, steps + 1);
    topDownMinSpeed = Tensors.vector(i -> maxSpeed, steps + 1);
    completionIndex = Tensors.vector(i -> RealScalar.ZERO, steps + 1);
    {
      // UI
      JPanel jPanel = new JPanel(new GridLayout(2, 2));
      // button for previous test
      JButton prev = new JButton("previous");
      prev.addActionListener(actionEvent -> previous());
      jPanel.add(prev);
      // button for next test
      JButton next = new JButton("next");
      next.addActionListener(actionEvent -> next());
      jPanel.add(next);
      // up button
      {
        JToggleButton jToggleButton = new JToggleButton("up");
        jToggleButton.addActionListener(actionEvent -> {
          switchupdown(jToggleButton.isSelected());
          jToggleButton.setText(jToggleButton.isSelected() ? "down" : "up");
        });
        jPanel.add(jToggleButton);
      }
      // text area
      textarea = new JTextArea(completionIndex.toString());
      textarea.setLineWrap(true);
      jPanel.add(textarea);
      jFrame.setContentPane(jPanel);
    }
    windowConfiguration.attach(getClass(), jFrame);
    jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    jFrame.setVisible(true);
    updateText();
  }

  private void next() {
    if (currentInd < steps)
      ++currentInd;
    updateText();
  }

  private void previous() {
    if (currentInd > 0)
      --currentInd;
    updateText();
  }

  private void switchupdown(boolean isSelected) {
    up = !isSelected;
    updateText();
  }

  private void updateText() {
    String approachText;
    if (up) {
      approachText = "Acceleration test.\n";
    } else {
      approachText = "Deceleration test.\n";
    }
    textarea.setText(approachText + motorCurrentValues.Get(currentInd) + "\n" + bottomUpMaxSpeed.toString() + "\n" + topDownMinSpeed.toString() + "\n"
        + completionIndex.toString());
  }

  @Override // from AbstractModule
  void protected_last() {
    RimoSocket.INSTANCE.removePutProvider(this);
    RimoSocket.INSTANCE.removeGetListener(this);
    // ---
    jFrame.setVisible(false);
    jFrame.dispose();
  }

  private static final int textintervall = 10;
  private int count = 0;

  /***************************************************/
  @Override // from GuideJoystickModule
  Optional<RimoPutEvent> control( //
      SteerColumnInterface steerColumnInterface, ManualControlInterface manualControlInterface) {
    short arms_raw = 0;
    if (manualControlInterface.isAutonomousPressed()) {
      // logic
      if (up) {
        Boolean speedThreshold = Scalars.lessThan( //
            meanTangentSpeed.add(speedMargin), //
            bottomUpMaxSpeed.Get(currentInd));
        if (!slowDownCompleted) {
          // we have to slow down to last value first
          arms_raw = Magnitude.ARMS.toShort(minPower);
          slowDownCompleted = !speedThreshold;
        } else if (Scalars.lessThan(bottomUpMaxSpeed.Get(currentInd), maxSpeed.add(speedMargin))) {
          // we are accelerating up
          // are we slower than last max tested value
          if (speedThreshold) {
            // accelerate with max power
            arms_raw = Magnitude.ARMS.toShort(maxPower);
          } else {
            // accelerate with selected power
            arms_raw = Magnitude.ARMS.toShort(motorCurrentValues.Get(currentInd));
            bottomUpMaxSpeed.set(meanTangentSpeed, currentInd);
          }
        }
      } else { // !up
        if (Scalars.lessThan(minSpeed.subtract(speedMargin), topDownMinSpeed.Get(currentInd))) {
          // we are slowing down
          // are we slower than last tested min value
          if (!slowDownTriggered) {
            // accelerate with max power
            arms_raw = Magnitude.ARMS.toShort(maxPower);
            slowDownTriggered = !Scalars.lessThan(//
                meanTangentSpeed.subtract(speedMargin), //
                topDownMinSpeed.Get(currentInd));
          } else {
            // decelerate with selected power
            arms_raw = Magnitude.ARMS.toShort(motorCurrentValues.Get(currentInd));
            topDownMinSpeed.set(meanTangentSpeed, currentInd);
          }
        }
      }
    } else { // !manualControlInterface.isAutonomousPressed()
      slowDownTriggered = false;
      slowDownCompleted = false;
      Scalar ahead = Differences.of(manualControlInterface.getAheadPair_Unit()).Get(0) //
          .multiply(ManualConfig.GLOBAL.torqueLimit);
      arms_raw = Magnitude.ARMS.toShort(ahead);
    }
    if (count++ > textintervall) {
      count = 0;
      updateCompletion();
      updateText();
    }
    return Optional.of(RimoPutHelper.operationTorque( //
        (short) -arms_raw, // sign left invert
        (short) +arms_raw)); // sign right id
  }

  private void updateCompletion() {
    // compute completion
    Scalar range = maxSpeed.subtract(minSpeed);
    Scalar completedRange = //
        bottomUpMaxSpeed.Get(currentInd).subtract(minSpeed)//
            .add(maxSpeed.subtract(bottomUpMaxSpeed.Get(currentInd)));
    completionIndex.set(completedRange.divide(range), currentInd);
  }

  @Override
  public void getEvent(RimoGetEvent getEvent) {
    meanTangentSpeed = ChassisGeometry.GLOBAL.odometryTangentSpeed(getEvent);
  }
}