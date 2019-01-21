// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import ch.ethz.idsc.gokart.core.AutoboxSocket;
import ch.ethz.idsc.gokart.dev.steer.SteerColumnTracker;
import ch.ethz.idsc.gokart.dev.steer.SteerConfig;
import ch.ethz.idsc.gokart.dev.steer.SteerGetEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerPositionControl;
import ch.ethz.idsc.gokart.dev.steer.SteerPutEvent;
import ch.ethz.idsc.gokart.dev.steer.SteerSocket;
import ch.ethz.idsc.gokart.gui.ControllerInfoPublish;
import ch.ethz.idsc.retina.util.data.Word;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;

/* package */ class SteerComponent extends AutoboxTestingComponent<SteerGetEvent, SteerPutEvent> {
  private static final int RESOLUTION = 1000;
  // ---
  private final SteerInitButton steerInitButton = new SteerInitButton();
  private final JToggleButton jToggleController = new JToggleButton("controller");
  private final SpinnerLabel<Word> spinnerLabelLw = new SpinnerLabel<>();
  private final SliderExt sliderPosition;
  private final JTextField torquePut;
  private final JTextField rangeWidth;
  private final JTextField rangePos;
  private final JTextField[] jTextFields = new JTextField[11];
  private final SteerPositionControl steerPositionControl = new SteerPositionControl();
  private final JButton stepLeft = new JButton("Left");
  private final JButton stepRight = new JButton("Right");
  private final JButton stepReset = new JButton("Reset");
  // ---
  private final SteerColumnTracker steerColumnTracker = SteerSocket.INSTANCE.getSteerColumnTracker();

  public SteerComponent() {
    { // calibration and controller
      JToolBar jToolBar = createRow("Mode");
      jToolBar.add(steerInitButton.getComponent());
      // ---
      jToggleController.setEnabled(false);
      jToolBar.add(jToggleController);
    }
    {
      JToolBar jToolBar = createRow("Command");
      spinnerLabelLw.setList(SteerPutEvent.COMMANDS);
      spinnerLabelLw.setValueSafe(SteerPutEvent.COMMANDS.get(1));
      spinnerLabelLw.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    { // command speed
      JToolBar jToolBar = createRow("Position");
      sliderPosition = SliderExt.wrap(new JSlider(-RESOLUTION, RESOLUTION, 0));
      sliderPosition.addToComponent(jToolBar);
    }
    {
      torquePut = createReading("Torque");
      rangeWidth = createReading("RangeWidth");
      rangePos = createReading("RangePos");
    }
    {
      JToolBar jToolBar = createRow("Step");
      jToolBar.add(stepLeft);
      stepLeft.addActionListener(actionEvent -> {
        double stepOfLimit = SteerConfig.GLOBAL.stepOfLimit.number().doubleValue();
        sliderPosition.jSlider.setValue((int) (-RESOLUTION * stepOfLimit));
      });
      jToolBar.add(stepRight);
      stepRight.addActionListener(actionEvent -> {
        double stepOfLimit = SteerConfig.GLOBAL.stepOfLimit.number().doubleValue();
        sliderPosition.jSlider.setValue((int) (+RESOLUTION * stepOfLimit));
      });
      jToolBar.add(stepReset);
      stepReset.addActionListener(actionEvent -> sliderPosition.jSlider.setValue(0));
    }
    addSeparator();
    { // reception
      jTextFields[0] = createReading("motAsp_CANInput");
      jTextFields[1] = createReading("motAsp_Qual");
      jTextFields[2] = createReading("tsuTrq_CANInput");
      jTextFields[3] = createReading("tsuTrq_Qual");
      jTextFields[4] = createReading("refMotTrq_CANInput");
      jTextFields[5] = createReading("estMotTrq_CANInput");
      jTextFields[6] = createReading("estMotTrq_Qual"); // operation state {0f, 1f, 2f}
      jTextFields[7] = createReading("gcpRelRckPos");
      jTextFields[8] = createReading("gcpRelRckQual");
      jTextFields[9] = createReading("gearRat");
      jTextFields[10] = createReading("halfRckPos");
    }
  }

  @Override // from GetListener
  public void getEvent(SteerGetEvent steerGetEvent) {
    steerInitButton.updateEnabled();
    final boolean isCalibrated = steerColumnTracker.isSteerColumnCalibrated();
    final boolean isHealthy = steerColumnTracker.isCalibratedAndHealthy();
    jToggleController.setEnabled(isCalibrated);
    // ---
    {
      rangeWidth.setText("" + steerColumnTracker.getIntervalWidth());
      rangeWidth.setBackground(isHealthy ? Color.GREEN : Color.RED);
      String angle = isCalibrated //
          ? steerColumnTracker.getSteerColumnEncoderCentered().toString() //
          : "NOT CALIBRATED";
      rangePos.setText(angle);
    }
    // ---
    jTextFields[0].setText("" + steerGetEvent.motAsp_CANInput);
    jTextFields[1].setText("" + steerGetEvent.motAsp_Qual);
    jTextFields[2].setText("" + steerGetEvent.tsuTrq_CANInput);
    jTextFields[3].setText("" + steerGetEvent.tsuTrq_Qual);
    jTextFields[4].setText("" + steerGetEvent.refMotTrq_CANInput);
    jTextFields[5].setText("" + steerGetEvent.estMotTrq_CANInput);
    {
      JTextField jTextField = jTextFields[6];
      boolean isActive = steerGetEvent.isActive();
      jTextField.setText(steerGetEvent.estMotTrq_Qual + " " + isActive);
      jTextField.setBackground(isActive ? Color.GREEN : Color.YELLOW);
    }
    jTextFields[7].setText("" + steerGetEvent.getGcpRelRckPos());
    {
      JTextField jTextField = jTextFields[8];
      boolean status = steerGetEvent.isRelRckQual();
      jTextField.setText(steerGetEvent.gcpRelRckQual + " " + status);
      jTextField.setBackground(status ? Color.GREEN : Color.YELLOW);
    }
    jTextFields[9].setText("" + steerGetEvent.gearRat);
    jTextFields[10].setText("" + steerGetEvent.halfRckPos);
  }

  @Override // from PutListener
  public void putEvent(SteerPutEvent putEvent) {
    torquePut.setText(putEvent.getTorque().toString());
  }

  @Override // from PutProvider
  public Optional<SteerPutEvent> putEvent() {
    if (steerColumnTracker.isSteerColumnCalibrated()) {
      final Scalar currAngle = steerColumnTracker.getSteerColumnEncoderCentered(); // SCE
      Scalar desPos = RationalScalar.of(-sliderPosition.jSlider.getValue(), RESOLUTION) //
          .multiply(SteerConfig.GLOBAL.columnMax);
      // System.out.println("here " + desPos);
      Scalar errPos = desPos.subtract(currAngle);
      Scalar torqueCmd = steerPositionControl.iterate(errPos);
      ControllerInfoPublish.publish(desPos, currAngle); // TODO not permanent, only for tuning
      if (jToggleController.isSelected())
        return Optional.of(SteerPutEvent.create(spinnerLabelLw.getValue(), torqueCmd));
    }
    return Optional.empty();
  }

  @Override
  protected AutoboxSocket<SteerGetEvent, SteerPutEvent> getSocket() {
    return SteerSocket.INSTANCE;
  }
}
