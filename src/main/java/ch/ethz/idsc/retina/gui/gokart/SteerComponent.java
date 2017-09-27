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
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ColorFormat;
import idsc.BinaryBlob;
import lcm.lcm.LCM;

class SteerComponent extends AutoboxTestingComponent<SteerGetEvent, SteerPutEvent> {
  public static final int RESOLUTION = 1000;
  public static final double MAX_TORQUE = 0.5;
  public static final double MAX_ANGLE = 0.6743167638778687;
  // ---
  private final JToggleButton enable = new JToggleButton("controller");
  private final JTextField kpConst = new JTextField();
  private final JTextField kdConst = new JTextField();
  private final SpinnerLabel<Word> spinnerLabelLw = new SpinnerLabel<>();
  private final SliderExt sliderExtTorque;
  private final JTextField[] jTextField = new JTextField[11];
  private final SteerAngleTracker steerAngleTracker = new SteerAngleTracker();
  private final PDSteerPositionControl positionController = new PDSteerPositionControl();
  private final JButton stepLeft = new JButton("step Left");
  private final JButton stepRight = new JButton("step Right");
  private final JButton resetSteps = new JButton("reset Steps");
  private boolean leftStepActive = false;
  private boolean rightStepActive = false;

  public SteerComponent() {
    {
      JToolBar jToolBar = createRow("command");
      jToolBar.add(enable);
    }
    {
      JToolBar jToolBar = createRow("Kp");
      kpConst.setPreferredSize(new Dimension(180, 28));
      kpConst.setText("" + positionController.Kp);
      kpConst.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          positionController.Kp = Double.parseDouble(kpConst.getText());
        }
      });
      jToolBar.add(kpConst);
    }
    {
      JToolBar jToolBar = createRow("Kd");
      kdConst.setPreferredSize(new Dimension(180, 28));
      kdConst.setText("" + positionController.Kd);
      kdConst.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          positionController.Kd = Double.parseDouble(kdConst.getText());
        }
      });
      jToolBar.add(kdConst);
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
    steerAngleTracker.getEvent(steerGetEvent);
    double angle = steerAngleTracker.getSteeringAngleRelative(steerGetEvent);
    // ---
    jTextField[0].setText("" + steerGetEvent.motAsp_CANInput);
    jTextField[1].setText("" + steerGetEvent.motAsp_Qual);
    jTextField[2].setText("" + steerGetEvent.tsuTrq_CANInput);
    jTextField[3].setText("" + steerGetEvent.tsuTrq_Qual);
    jTextField[4].setText("" + steerGetEvent.refMotTrq_CANInput);
    jTextField[5].setText("" + steerGetEvent.estMotTrq_CANInput);
    jTextField[6].setText("" + steerGetEvent.estMotTrq_Qual);
    jTextField[7].setText("" + steerGetEvent.gcpRelRckPos + " " + angle);
    {
      Color color = ColorFormat.toColor(ColorDataGradients.THERMOMETER.apply(RealScalar.of((angle + 1) / 2)));
      jTextField[7].setBackground(color);
    }
    jTextField[8].setText("" + steerGetEvent.gcpRelRckQual);
    jTextField[9].setText("" + steerGetEvent.gearRat);
    jTextField[10].setText("" + steerGetEvent.halfRckPos);
  }

  private static Scalar giveTorque(int value) {
    return RealScalar.of(value * MAX_TORQUE / RESOLUTION);
  }

  @Override
  public void putEvent(SteerPutEvent putEvent) {
    // TODO NRJ Auto-generated method stub
  }

  @Override
  public Optional<SteerPutEvent> putEvent() {
    double currAngle = steerAngleTracker.getCurrAngle();
    double desPos = sliderExtTorque.jSlider.getValue() * MAX_ANGLE / RESOLUTION;
    if (leftStepActive)
      desPos = -0.65;
    if (rightStepActive)
      desPos = 0.65;
    // System.out.println(desPos);
    double errPos = desPos - currAngle;
    double cmd = positionController.iterate(errPos);
    // System.out.println(Tensors.vector(cmd, currAngle).map(Round._3));
    {
      BinaryBlob binaryBlob = new BinaryBlob();
      binaryBlob.data = new byte[8];
      binaryBlob.data_length = 8;
      ByteBuffer byteBuffer = ByteBuffer.wrap(binaryBlob.data);
      byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
      byteBuffer.putDouble(errPos);
      try {
        LCM.getSingleton().publish("myChannel", binaryBlob);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    if (enable.isSelected()) {
      return Optional.of(new SteerPutEvent(spinnerLabelLw.getValue(), cmd));
    }
    return Optional.empty();
  }
}
