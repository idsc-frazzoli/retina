// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import java.awt.Dimension;
import java.util.Objects;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JToolBar;

import ch.ethz.idsc.gokart.core.AutoboxSocket;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoPutTire;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;

/* package */ class RimoComponent extends AutoboxTestingComponent<RimoGetEvent, RimoPutEvent> {
  private final RimoPutFields rimoPutFieldsL = new RimoPutFields();
  private final RimoPutFields rimoPutFieldsR = new RimoPutFields();
  private final RimoGetFields rimoGetFieldsL = new RimoGetFields();
  private final RimoGetFields rimoGetFieldsR = new RimoGetFields();
  /** default message used only for display information */
  private RimoGetEvent rimoGetEvent;

  private void setZero() {
    rimoPutFieldsL.setZero();
    rimoPutFieldsR.setZero();
  }

  public RimoComponent() {
    { // STOP BUTTON
      JToolBar jToolBar = createRow("Actions");
      JButton stopButton = new JButton("STOP");
      jToolBar.add(stopButton);
      stopButton.addActionListener(e -> setZero());
    }
    assign(rimoPutFieldsL, "LEFT");
    addSeparator();
    assign(rimoPutFieldsR, "RIGHT");
    // reception
    addSeparator();
    assign(rimoGetFieldsL, "LEFT");
    addSeparator();
    assign(rimoGetFieldsR, "RIGHT");
  }

  private void assign(RimoPutFields rimoPutFields, String side) {
    { // operation mode
      JToolBar jToolBar = createRow(side + " command");
      rimoPutFields.spinnerLabelCmd.setList(RimoPutTire.COMMANDS);
      rimoPutFields.spinnerLabelCmd.setValueSafe(RimoPutTire.OPERATION);
      rimoPutFields.spinnerLabelCmd.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    { // command speed
      JToolBar jToolBar = createRow(side + " speed");
      rimoPutFields.sliderExtVel.addToComponent(jToolBar);
    }
    { // command torque
      JToolBar jToolBar = createRow(side + " torque");
      rimoPutFields.sliderExtTrq.addToComponent(jToolBar);
    }
    {// TRIGGER
      JToolBar jToolBar = createRow("Trigger");
      rimoPutFields.spinnerLabelTrigger.setList(RimoPutTire.TRIGGERS);
      rimoPutFields.spinnerLabelTrigger.setValueSafe(RimoPutTire.TRIG_OFF);
      rimoPutFields.spinnerLabelTrigger.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    { // SDO COMMAND
      JToolBar jToolBar = createRow("SDO command");
      // rimoPutFields.jTextfieldSdoCommand.setMinimumSize(new Dimension(130, 28));
      jToolBar.add(rimoPutFields.jTextfieldSdoCommand);
      rimoPutFields.jTextfieldSdoCommand.setText("0");
      jToolBar.add(rimoPutFields.jTextfieldSdoMainIndex); // SDO MAIN INDEX
      rimoPutFields.jTextfieldSdoMainIndex.setText("0");
      jToolBar.add(rimoPutFields.jTextfieldSdoSubIndex); // SDO SUBINDEX
      rimoPutFields.jTextfieldSdoSubIndex.setText("0");
    }
    { // SDO DATA
      JToolBar jToolBar = createRow("SDO data");
      jToolBar.add(rimoPutFields.jTextfieldSdoData);
      rimoPutFields.jTextfieldSdoData.setText("0");
    }
  }

  private void assign(RimoGetFields rimoGetFields, String side) {
    rimoGetFields.jTF_status_word = createReading(side + " status word");
    rimoGetFields.jTF_actual_speed = createReading(side + " actual speed");
    rimoGetFields.jTF_rms_motor_current = createReading(side + " rms current");
    rimoGetFields.jTF_dc_bus_voltage = createReading(side + " dc bus voltage");
    rimoGetFields.jTF_error_code = createReading(side + " error code");
    rimoGetFields.jTF_error_code_emergency = createReading(side + " emergcy");
    rimoGetFields.jTF_temperature_motor = createReading(side + " temp. motor");
    rimoGetFields.jTF_temperature_heatsink = createReading(side + " temp. heatsink");
    rimoGetFields.jTF_SdoMessage = createReading(side + " SDO message");
  }

  @Override
  public void getEvent(RimoGetEvent rimoGetEvent) {
    this.rimoGetEvent = rimoGetEvent;
    rimoGetFieldsL.updateText(rimoGetEvent.getTireL);
    rimoGetFieldsR.updateText(rimoGetEvent.getTireR);
  }

  @Override
  public void putEvent(RimoPutEvent rimoPutEvent) {
    /** as long as there is only 1 valid command word,
     * there is no need to update the spinner label */
    rimoPutFieldsL.updateGuiElements(rimoPutEvent.putTireL);
    rimoPutFieldsR.updateGuiElements(rimoPutEvent.putTireR);
    // ---
    if (Objects.nonNull(rimoGetEvent)) { // may not be received yet
      rimoGetFieldsL.updateRateColor(rimoGetEvent.getTireL);
      rimoGetFieldsR.updateRateColor(rimoGetEvent.getTireR);
    }
  }

  @Override
  public Optional<RimoPutEvent> putEvent() {
    return Optional.of(new RimoPutEvent( //
        rimoPutFieldsL.getPutTire(), //
        rimoPutFieldsR.getPutTire()));
  }

  @Override
  protected AutoboxSocket<RimoGetEvent, RimoPutEvent> getSocket() {
    return RimoSocket.INSTANCE;
  }
}
