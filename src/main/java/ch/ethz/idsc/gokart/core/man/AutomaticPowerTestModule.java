package ch.ethz.idsc.gokart.core.man;

import java.awt.GridLayout;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetListener;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutHelper;
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
  Tensor motorCurrentValues;
  Tensor bottomUpMaxSpeed;
  Tensor topDownMinSpeed;
  Tensor completionIndex;
  Scalar maxSpeed = Quantity.of(10, SI.VELOCITY);
  Scalar minSpeed = Quantity.of(10, SI.VELOCITY);
  Scalar speedMargin = Quantity.of(1, SI.VELOCITY);
  Scalar maxPower;
  Scalar minPower;
  int steps = 20;
  int currentInd = 1;
  boolean up = true;
  private Scalar meanTangentSpeed = Quantity.of(0, SI.VELOCITY);
  private final JFrame jFrame = new JFrame();
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());
  private JButton prev;
  private JButton next;
  private JButton upbutton;
  private JTextArea textarea;

  @Override // from AbstractModule
  void protected_first() {
    RimoSocket.INSTANCE.addPutProvider(this);
    RimoSocket.INSTANCE.addGetListener(this);
    Scalar maxPower = Quantity.of(2300, NonSI.ARMS);
    Scalar minPower = Quantity.of(-2300, NonSI.ARMS);
    motorCurrentValues = Subdivide.of(minPower, maxPower, steps);
    bottomUpMaxSpeed = Tensors.vector((i) -> minSpeed, steps + 1);
    bottomUpMaxSpeed = Tensors.vector((i) -> maxSpeed, steps + 1);
    completionIndex = Tensors.vector((i) -> RealScalar.ZERO, steps + 1);
    {
      // UI
      JPanel jPanel = new JPanel(new GridLayout(2, 2));
      // button for previous test
      prev = new JButton("previous");
      prev.addActionListener(actionEvent -> previous());
      jPanel.add(prev);
      // button for next test
      next = new JButton("next");
      next.addActionListener(actionEvent -> next());
      jPanel.add(next);
      // up button
      upbutton = new JButton("up");
      upbutton.addActionListener(ActionEvent -> switchupdown());
      jPanel.add(upbutton);
      // text area
      textarea = new JTextArea(completionIndex.toString());
      jPanel.add(textarea);
      jFrame.setContentPane(jPanel);
    }
    windowConfiguration.attach(getClass(), jFrame);
    jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    jFrame.setVisible(true);
    updateText();
  }

  void next() {
    if (currentInd < steps)
      currentInd += 1;
    updateText();
  }

  void previous() {
    if (currentInd > 0)
      currentInd -= 1;
    updateText();
  }

  void switchupdown() {
    up = !up;
    if (up) {
      upbutton.setText("up");
    } else {
      upbutton.setText("down");
    }
    updateText();
  }

  void updateText() {
    String approachText;
    if (up) {
      approachText = "Acceleration test.\n";
    } else {
      approachText = "Deceleration test.\n";
    }
    textarea.setText(approachText + motorCurrentValues.Get(currentInd) + "\n" + completionIndex.toString());
  }

  @Override // from AbstractModule
  void protected_last() {
    RimoSocket.INSTANCE.removePutProvider(this);
    RimoSocket.INSTANCE.removeGetListener(this);
    // ---
    jFrame.setVisible(false);
    jFrame.dispose();
  }

  int textintervall = 100;
  int count = 0;

  /***************************************************/
  @Override // from GuideJoystickModule
  Optional<RimoPutEvent> control(//
      SteerColumnInterface steerColumnInterface, ManualControlInterface manualControlInterface) {
    short arms_raw = 0;
    if (manualControlInterface.isAutonomousPressed()) {
      // logic
      if (up && Scalars.lessThan(bottomUpMaxSpeed.Get(currentInd), maxSpeed.add(speedMargin))) {
        // we are accelerating up
        // are we slower than last max tested value
        if (Scalars.lessThan(//
            meanTangentSpeed.add(speedMargin), //
            bottomUpMaxSpeed.Get(currentInd))) {
          // accelerate with max power
          arms_raw = Magnitude.ARMS.toShort(maxPower);
        } else {
          // accelerate with selected power
          arms_raw = Magnitude.ARMS.toShort(motorCurrentValues.Get(currentInd));
          bottomUpMaxSpeed.set(meanTangentSpeed, currentInd);
        }
      } else if (Scalars.lessThan(minSpeed, topDownMinSpeed.Get(currentInd))) {
        // we are slowing down
        // are we slower than last tested min value
        if (Scalars.lessThan(//
            meanTangentSpeed.subtract(speedMargin), topDownMinSpeed.Get(currentInd))) {
          // accelerate with max power
          arms_raw = Magnitude.ARMS.toShort(maxPower);
        } else {
          // decelerate with selected power
          arms_raw = Magnitude.ARMS.toShort(motorCurrentValues.Get(currentInd));
          topDownMinSpeed.set(meanTangentSpeed, currentInd);
        }
      }
      arms_raw = Magnitude.ARMS.toShort(motorCurrentValues.Get(currentInd));
    } else {
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