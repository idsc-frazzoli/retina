// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import ch.ethz.idsc.retina.dev.joystick.GenericXboxPadJoystick;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.retina.dev.linmot.LinmotInitProcedure;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutConfiguration;
import ch.ethz.idsc.retina.dev.linmot.LinmotPutEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotSocket;
import ch.ethz.idsc.retina.dev.linmot.TimedPutEvent;
import ch.ethz.idsc.retina.util.data.Word;
import ch.ethz.idsc.retina.util.gui.SpinnerLabel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ColorFormat;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;

public class LinmotComponent extends InterfaceComponent implements LinmotGetListener {
  private TimerTask timerTask = null;
  private final JButton initButton = new JButton("Init");
  private final SpinnerLabel<Word> spinnerLabelCtrl = new SpinnerLabel<>();
  private final SpinnerLabel<Word> spinnerLabelHdr = new SpinnerLabel<>();
  private final SliderExt sliderExtTPos;
  private final SliderExt sliderExtMVel;
  private final SliderExt sliderExtAcc;
  private final SliderExt sliderExtDec;
  // ---
  private final JTextField jTextFieldStatusWord;
  private final JTextField jTextFieldStateVariable;
  private final JTextField jTextFieldActualPosition;
  private final JTextField jTextFieldDemandPosition;
  private final JTextField jTextFieldWindingTemp1;
  private final JTextField jTextFieldWindingTemp2;
  public final Queue<TimedPutEvent<LinmotPutEvent>> queue = new PriorityQueue<>();

  public LinmotComponent() {
    {
      JToolBar jToolBar = createRow("Special Routines");
      initButton.setEnabled(false);
      initButton.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          if (queue.isEmpty())
            queue.addAll(new LinmotInitProcedure().list);
          else
            System.out.println("queue not empty yet");
        }
      });
      jToolBar.add(initButton);
    }
    {
      JToolBar jToolBar = createRow("control word");
      spinnerLabelCtrl.setList(LinmotPutConfiguration.COMMANDS);
      spinnerLabelCtrl.setValueSafe(LinmotPutConfiguration.COMMANDS.get(1));
      spinnerLabelCtrl.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    { // command speed
      JToolBar jToolBar = createRow("motion cmd hdr");
      spinnerLabelHdr.setList(LinmotPutConfiguration.HEADER);
      spinnerLabelHdr.setValueSafe(LinmotPutConfiguration.HEADER.get(0));
      spinnerLabelHdr.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    { // target pos
      JToolBar jToolBar = createRow("target pos");
      sliderExtTPos = SliderExt.wrap(new JSlider( //
          LinmotPutConfiguration.TARGETPOS_MIN, //
          LinmotPutConfiguration.TARGETPOS_MAX, //
          LinmotPutConfiguration.TARGETPOS_INIT));
      sliderExtTPos.addToComponent(jToolBar);
      // sliderExtF2.setValueShort(init.target_position);
    }
    { // max velocity
      JToolBar jToolBar = createRow("max velocity");
      sliderExtMVel = SliderExt.wrap(new JSlider( //
          LinmotPutConfiguration.MAXVELOCITY_MIN, //
          LinmotPutConfiguration.MAXVELOCITY_MAX, //
          LinmotPutConfiguration.MAXVELOCITY_INIT));
      sliderExtMVel.addToComponent(jToolBar);
    }
    { // acceleration
      JToolBar jToolBar = createRow("acceleration");
      sliderExtAcc = SliderExt.wrap(new JSlider( //
          LinmotPutConfiguration.ACCELERATION_MIN, //
          LinmotPutConfiguration.ACCELERATION_MAX, //
          LinmotPutConfiguration.ACCELERATION_INIT));
      sliderExtAcc.addToComponent(jToolBar);
    }
    { // deceleration
      JToolBar jToolBar = createRow("deceleration");
      sliderExtDec = SliderExt.wrap(new JSlider( //
          LinmotPutConfiguration.DECELERATION_MIN, //
          LinmotPutConfiguration.DECELERATION_MAX, //
          LinmotPutConfiguration.DECELERATION_INIT));
      sliderExtDec.addToComponent(jToolBar);
    }
    addSeparator();
    {
      jTextFieldStatusWord = createReading("status word");
      jTextFieldStateVariable = createReading("state variable");
      jTextFieldActualPosition = createReading("actual pos.");
      jTextFieldDemandPosition = createReading("demand pos.");
      jTextFieldWindingTemp1 = createReading("winding temp.1");
      jTextFieldWindingTemp2 = createReading("winding temp.2");
    }
  }

  @Override
  public void connectAction(int period, boolean isSelected) {
    initButton.setEnabled(isSelected);
    if (isSelected) {
      LinmotSocket.INSTANCE.start();
      timerTask = new TimerTask() {
        @Override
        public void run() {
          initButton.setEnabled(queue.isEmpty());
          final LinmotPutEvent linmotPutEvent;
          if (queue.isEmpty()) {
            linmotPutEvent = new LinmotPutEvent();
            linmotPutEvent.control_word = spinnerLabelCtrl.getValue().getShort();
            linmotPutEvent.motion_cmd_hdr = spinnerLabelHdr.getValue().getShort();
            linmotPutEvent.target_position = (short) sliderExtTPos.jSlider.getValue();
            linmotPutEvent.max_velocity = (short) sliderExtMVel.jSlider.getValue();
            linmotPutEvent.acceleration = (short) sliderExtAcc.jSlider.getValue();
            linmotPutEvent.deceleration = (short) sliderExtDec.jSlider.getValue();
          } else {
            TimedPutEvent<LinmotPutEvent> timedLinmotPutEvent = queue.peek();
            // System.out.println(timedLinmotPutEvent.linmotPutEvent.control_word);
            if (timedLinmotPutEvent.time_ms < System.currentTimeMillis()) {
              queue.poll();
            }
            linmotPutEvent = timedLinmotPutEvent.linmotPutEvent;
          }
          LinmotSocket.INSTANCE.send(linmotPutEvent);
        }
      };
      timer.schedule(timerTask, 100, period);
    } else {
      if (Objects.nonNull(timerTask)) {
        timerTask.cancel();
        timerTask = null;
      }
      LinmotSocket.INSTANCE.stop();
    }
  }

  @Override
  public void linmotGet(LinmotGetEvent linmotGetEvent) {
    // linmotGetEvent.toInfoString()
    jTextFieldStatusWord.setText(String.format("%04X", linmotGetEvent.status_word));
    jTextFieldStateVariable.setText(String.format("%04X", linmotGetEvent.state_variable));
    // TODO figure out units for position
    jTextFieldActualPosition.setText("" + linmotGetEvent.actual_position);
    jTextFieldDemandPosition.setText("" + linmotGetEvent.demand_position);
    {
      jTextFieldWindingTemp1.setText(Quantity.of(linmotGetEvent.windingTemperature1(), "[C]").toString());
      double temp1 = linmotGetEvent.windingTemperature1();
      Scalar scalar = RealScalar.of(temp1 / 100);
      scalar = Clip.unit().apply(scalar);
      Tensor vector = ColorDataGradients.THERMOMETER.apply(scalar);
      Color color = ColorFormat.toColor(vector);
      jTextFieldWindingTemp1.setBackground(color);
    }
    {
      jTextFieldWindingTemp2.setText(Quantity.of(linmotGetEvent.windingTemperature2(), "[C]").toString());
      double temp2 = linmotGetEvent.windingTemperature2();
      Scalar scalar = RealScalar.of(temp2 / 100);
      scalar = Clip.unit().apply(scalar);
      Tensor vector = ColorDataGradients.THERMOMETER.apply(scalar);
      Color color = ColorFormat.toColor(vector);
      jTextFieldWindingTemp2.setBackground(color);
    }
  }

  @Override
  public void joystick(JoystickEvent joystickEvent) {
    if (isJoystickEnabled()) {
      GenericXboxPadJoystick joystick = (GenericXboxPadJoystick) joystickEvent;
      double value = joystick.getLeftKnobDirectionDown();
      int pos = (int) //
      Math.min(Math.max(LinmotPutConfiguration.TARGETPOS_MIN, //
          (LinmotPutConfiguration.TARGETPOS_MIN * value + LinmotPutConfiguration.TARGETPOS_INIT)), //
          LinmotPutConfiguration.TARGETPOS_MAX);
      sliderExtTPos.jSlider.setValue(pos);
    }
  }
}
