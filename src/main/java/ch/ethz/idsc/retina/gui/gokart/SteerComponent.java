// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import ch.ethz.idsc.retina.dev.steer.PDSteerPositionControl;
import ch.ethz.idsc.retina.dev.steer.SteerAngleTracker;
import ch.ethz.idsc.retina.dev.steer.SteerGetEvent;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.util.data.Word;
import ch.ethz.idsc.retina.util.gui.SliderExt;
import ch.ethz.idsc.retina.util.gui.SpinnerLabel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ColorFormat;
import ch.ethz.idsc.tensor.sca.Round;
import idsc.BinaryBlob;
import lcm.lcm.LCM;

class SteerComponent extends AutoboxTestingComponent<SteerGetEvent, SteerPutEvent> {
  public static final int RESOLUTION = 1000;
  public static final double MAX_TORQUE = 0.5;
  // ---
  private final JToggleButton jToggleController = new JToggleButton("controller");
  private final JTextField kpConst = new JTextField();
  private final JTextField kdConst = new JTextField();
  private final JTextField tqLimit = new JTextField();
  private final SpinnerLabel<Word> spinnerLabelLw = new SpinnerLabel<>();
  private final SliderExt sliderExtTorque;
  private final JTextField[] jTextField = new JTextField[11];
  private final PDSteerPositionControl positionController = new PDSteerPositionControl();
  private final JButton stepLeft = new JButton("step Left");
  private final JButton stepRight = new JButton("step Right");
  private final JButton resetSteps = new JButton("reset Steps");
  private final JButton calibrate = new JButton("calibrate");
  private boolean leftStepActive = false;
  private boolean rightStepActive = false;
  private boolean calibrateReq = false;
  private boolean calibrated = false;
  private long startTime;
  
  public SteerComponent() {
    {
      JToolBar jToolBar = createRow("command");
      jToolBar.add(jToggleController);
    }
    {
      JToolBar jToolBar = createRow("Kp");
      kpConst.setPreferredSize(new Dimension(180, 28));
      kpConst.setText("" + positionController.Kp);
      kpConst.addActionListener(e -> positionController.Kp = Double.parseDouble(kpConst.getText()));
      jToolBar.add(kpConst);
    }
    {
      JToolBar jToolBar = createRow("Kd");
      kdConst.setPreferredSize(new Dimension(180, 28));
      kdConst.setText("" + positionController.Kd);
      kdConst.addActionListener(e -> positionController.Kd = Double.parseDouble(kdConst.getText()));
      jToolBar.add(kdConst);
    }
    {
      JToolBar jToolBar = createRow("Torque limit");
      tqLimit.setPreferredSize(new Dimension(180, 28));
      tqLimit.setText("" + positionController.torqueLimit);
      tqLimit.addActionListener(e -> positionController.torqueLimit = Double.parseDouble(tqLimit.getText()));
      jToolBar.add(tqLimit);
    }
    {
      JToolBar jToolBar = createRow("command");
      spinnerLabelLw.setList(SteerPutEvent.COMMANDS);
      spinnerLabelLw.setValueSafe(SteerPutEvent.CMD_ON);
      spinnerLabelLw.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    { // command speed
      JToolBar jToolBar = createRow("torque");
      sliderExtTorque = SliderExt.wrap(new JSlider(-RESOLUTION, RESOLUTION, 0));
      sliderExtTorque.addToComponent(jToolBar);
    }
    {
      JToolBar jToolBar = createRow("step");
      jToolBar.add(stepLeft);
      stepLeft.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          leftStepActive = true;
        }
      });
      jToolBar.add(stepRight);
      stepRight.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          rightStepActive = true;
        }
      });
      jToolBar.add(resetSteps);
      resetSteps.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          rightStepActive = false;
          leftStepActive = false;
        }
      });
    }
    {
      { // command speed
        JToolBar jToolBar = createRow("calibration");
        jToolBar.add(calibrate);
        calibrate.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            calibrateReq = true;
            startTime = System.currentTimeMillis();
          }
        });
      }
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

  @Override
  public void getEvent(SteerGetEvent steerGetEvent) {
    final boolean isCalibrated = SteerAngleTracker.INSTANCE.isCalibrated();
    final double angle = isCalibrated ? SteerAngleTracker.INSTANCE.getSteeringValue() : 0;
    final String descr = isCalibrated ? "" + angle : "CALIBRATION MISS";
    jToggleController.setEnabled(isCalibrated);
    // ---
    jTextField[0].setText("" + steerGetEvent.motAsp_CANInput);
    jTextField[1].setText("" + steerGetEvent.motAsp_Qual);
    jTextField[2].setText("" + steerGetEvent.tsuTrq_CANInput);
    jTextField[3].setText("" + steerGetEvent.tsuTrq_Qual);
    jTextField[4].setText("" + steerGetEvent.refMotTrq_CANInput);
    jTextField[5].setText("" + steerGetEvent.estMotTrq_CANInput);
    jTextField[6].setText("" + steerGetEvent.estMotTrq_Qual);
    jTextField[7].setText("" + steerGetEvent.gcpRelRckPos + " " + descr);
    if (isCalibrated) {
      Color color = ColorFormat.toColor(ColorDataGradients.THERMOMETER.apply(RealScalar.of((angle + 1) / 2)));
      jTextField[7].setBackground(color);
    }
    jTextField[8].setText("" + steerGetEvent.gcpRelRckQual);
    jTextField[9].setText("" + steerGetEvent.gearRat);
    jTextField[10].setText("" + steerGetEvent.halfRckPos);
  }

  @Override
  public void putEvent(SteerPutEvent putEvent) {
    // nothing to do here
  }

  @Override
  public Optional<SteerPutEvent> putEvent() {
    if (calibrateReq && !calibrated) {
      
      long currTime = System.currentTimeMillis();
      //System.out.println(Tensors.vector(currTime - startTime).map(Round._3));
      
      if (currTime - startTime < 1000)
        return Optional.of(new SteerPutEvent(spinnerLabelLw.getValue(), 0.1));
      
      if (currTime - startTime < 2000)
        return Optional.of(new SteerPutEvent(spinnerLabelLw.getValue(), 0.2));

      if (currTime - startTime < 4000)
        return Optional.of(new SteerPutEvent(spinnerLabelLw.getValue(), -0.1));
      
      if (currTime - startTime < 5000)
        return Optional.of(new SteerPutEvent(spinnerLabelLw.getValue(), -0.2));
      else
        calibrated = true;
    }
    
    if (SteerAngleTracker.INSTANCE.isCalibrated()) {
      final double currAngle = SteerAngleTracker.INSTANCE.getSteeringValue();
      double desPos = sliderExtTorque.jSlider.getValue() * SteerPutEvent.MAX_ANGLE / RESOLUTION;
      {
        if (leftStepActive)
          desPos = +0.65;
        if (rightStepActive)
          desPos = -0.65;
      }
      // System.out.println(desPos);
      double errPos = desPos - currAngle;
      final double torqueCmd = positionController.iterate(errPos);
      // System.out.println(Tensors.vector(currAngle).map(Round._3));
      {
        BinaryBlob binaryBlob = new BinaryBlob();
        binaryBlob.data = new byte[16];
        binaryBlob.data_length = 16;
        ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putDouble(desPos);
        byteBuffer.putDouble(currAngle);
        LCM.getSingleton().publish("myChannel", binaryBlob);
      }
      if (jToggleController.isSelected())
        return Optional.of(new SteerPutEvent(spinnerLabelLw.getValue(), torqueCmd));
    }
    return Optional.empty();
  }
}
