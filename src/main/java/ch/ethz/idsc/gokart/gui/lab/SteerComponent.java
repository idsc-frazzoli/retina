// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import ch.ethz.idsc.gokart.gui.ControllerInfoPublish;
import ch.ethz.idsc.retina.dev.steer.SteerColumnTracker;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.dev.steer.SteerGetEvent;
import ch.ethz.idsc.retina.dev.steer.SteerPositionControl;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.util.data.Word;
import ch.ethz.idsc.retina.util.gui.SliderExt;
import ch.ethz.idsc.retina.util.gui.SpinnerLabel;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Scalar;

/* package */ class SteerComponent extends AutoboxTestingComponent<SteerGetEvent, SteerPutEvent> {
  public static final int RESOLUTION = 1000;
  // ---
  public final SteerInitButton steerInitButton = new SteerInitButton();
  private final JToggleButton jToggleController = new JToggleButton("controller");
  private final SpinnerLabel<Word> spinnerLabelLw = new SpinnerLabel<>();
  private final SliderExt sliderPosition;
  private final JTextField torquePut;
  private final JTextField rangeWidth;
  private final JTextField rangePos;
  private final JTextField[] jTextField = new JTextField[11];
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
      spinnerLabelLw.setValueSafe(SteerPutEvent.CMD_ON);
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
      stepLeft.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent event) {
          double stepOfLimit = SteerConfig.GLOBAL.stepOfLimit.number().doubleValue();
          sliderPosition.jSlider.setValue((int) (-RESOLUTION * stepOfLimit));
        }
      });
      jToolBar.add(stepRight);
      stepRight.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent event) {
          double stepOfLimit = SteerConfig.GLOBAL.stepOfLimit.number().doubleValue();
          sliderPosition.jSlider.setValue((int) (+RESOLUTION * stepOfLimit));
        }
      });
      jToolBar.add(stepReset);
      stepReset.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent event) {
          sliderPosition.jSlider.setValue(0);
        }
      });
    }
    addSeparator();
    { // reception
      jTextField[0] = createReading("motAsp_CANInput");
      jTextField[1] = createReading("motAsp_Qual");
      jTextField[2] = createReading("tsuTrq_CANInput");
      jTextField[3] = createReading("tsuTrq_Qual");
      jTextField[4] = createReading("refMotTrq_CANInput");
      jTextField[5] = createReading("estMotTrq_CANInput");
      jTextField[6] = createReading("estMotTrq_Qual");
      jTextField[7] = createReading("gcpRelRckPos");
      jTextField[8] = createReading("gcpRelRckQual");
      jTextField[9] = createReading("gearRat");
      jTextField[10] = createReading("halfRckPos");
    }
  }

  @Override // from GetListener
  public void getEvent(SteerGetEvent steerGetEvent) {
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
    jTextField[0].setText("" + steerGetEvent.motAsp_CANInput);
    jTextField[1].setText("" + steerGetEvent.motAsp_Qual);
    jTextField[2].setText("" + steerGetEvent.tsuTrq_CANInput);
    jTextField[3].setText("" + steerGetEvent.tsuTrq_Qual);
    jTextField[4].setText("" + steerGetEvent.refMotTrq_CANInput);
    jTextField[5].setText("" + steerGetEvent.estMotTrq_CANInput);
    {
      boolean isActive = steerGetEvent.isActive();
      jTextField[6].setText("" + isActive);
      jTextField[6].setBackground(isActive ? Color.GREEN : Color.YELLOW);
    }
    jTextField[7].setText("" + steerGetEvent.getGcpRelRckPos());
    jTextField[8].setText("" + steerGetEvent.gcpRelRckQual);
    jTextField[9].setText("" + steerGetEvent.gearRat);
    jTextField[10].setText("" + steerGetEvent.halfRckPos);
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
}
