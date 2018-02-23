// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import ch.ethz.idsc.retina.dev.misc.MiscEmergencyBit;
import ch.ethz.idsc.retina.dev.misc.MiscGetEvent;
import ch.ethz.idsc.retina.dev.misc.MiscIgnitionProvider;
import ch.ethz.idsc.retina.dev.misc.MiscPutEvent;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.util.data.Word;
import ch.ethz.idsc.retina.util.gui.SpinnerLabel;
import ch.ethz.idsc.tensor.Scalar;

/* package */ class MiscComponent extends AutoboxTestingComponent<MiscGetEvent, MiscPutEvent> {
  public static final List<Word> COMMANDS = Arrays.asList( //
      Word.createByte("PASSIVE", (byte) 0), //
      Word.createByte("RESET", (byte) 1) //
  );
  public static final List<Word> LEDCONTROL = Arrays.asList( //
      Word.createByte("OFF", (byte) 0), //
      Word.createByte("ON", (byte) 1) //
  );
  // ---
  private final JButton jButtonCommReset = new JButton("Reset");
  private final SpinnerLabel<Word> spinnerLabelRimoL = new SpinnerLabel<>();
  private final SpinnerLabel<Word> spinnerLabelRimoR = new SpinnerLabel<>();
  private final SpinnerLabel<Word> spinnerLabelLinmot = new SpinnerLabel<>();
  private final SpinnerLabel<Word> spinnerLabelSteer = new SpinnerLabel<>();
  private final SpinnerLabel<Word> spinnerLabelLed = new SpinnerLabel<>();
  private final JTextField jTextFieldEmg;
  private final JCheckBox[] jCheckBoxStatusWord = new JCheckBox[2];
  private final JTextField jTextFieldBat;

  public MiscComponent() {
    {
      JToolBar jToolBar = createRow("Communication");
      // jButtonCommReset.setEnabled(false);
      jButtonCommReset.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent event) {
          MiscIgnitionProvider.INSTANCE.schedule();
        }
      });
      jToolBar.add(jButtonCommReset);
    }
    {
      JToolBar jToolBar = createRow("Reset RimoL");
      spinnerLabelRimoL.setList(COMMANDS);
      spinnerLabelRimoL.setValueSafe(COMMANDS.get(0));
      spinnerLabelRimoL.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    {
      JToolBar jToolBar = createRow("Reset RimoR");
      spinnerLabelRimoR.setList(COMMANDS);
      spinnerLabelRimoR.setValueSafe(COMMANDS.get(0));
      spinnerLabelRimoR.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    {
      JToolBar jToolBar = createRow("Reset Linmot");
      spinnerLabelLinmot.setList(COMMANDS);
      spinnerLabelLinmot.setValueSafe(COMMANDS.get(0));
      spinnerLabelLinmot.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    {
      JToolBar jToolBar = createRow("Reset Steer");
      spinnerLabelSteer.setList(COMMANDS);
      spinnerLabelSteer.setValueSafe(COMMANDS.get(0));
      spinnerLabelSteer.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    {
      JToolBar jToolBar = createRow("Led");
      spinnerLabelLed.setList(LEDCONTROL);
      spinnerLabelLed.setValueSafe(LEDCONTROL.get(0));
      spinnerLabelLed.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    addSeparator();
    { // reception
      jTextFieldEmg = createReading("Emergency");
      for (MiscEmergencyBit meb : MiscEmergencyBit.values())
        jCheckBoxStatusWord[meb.ordinal()] = //
            createReadingCheckbox(meb.ordinal() + " " + meb);
      jTextFieldBat = createReading("Battery");
    }
  }

  @Override // from GetListener
  public void getEvent(MiscGetEvent miscGetEvent) {
    jButtonCommReset.setEnabled(miscGetEvent.isCommTimeout());
    // ---
    {
      jTextFieldEmg.setText(String.format("0x%02x", miscGetEvent.getEmergency()));
      Color color = miscGetEvent.isEmergency() ? Color.RED : Color.WHITE;
      jTextFieldEmg.setBackground(color);
    }
    for (MiscEmergencyBit meb : MiscEmergencyBit.values()) {
      boolean selected = (miscGetEvent.getEmergency() & (1 << meb.ordinal())) != 0;
      jCheckBoxStatusWord[meb.ordinal()].setSelected(selected);
    }
    {
      Scalar voltage = miscGetEvent.getSteerBatteryVoltage();
      jTextFieldBat.setText(voltage.toString());
      boolean isInside = SteerConfig.GLOBAL.operatingVoltageClip().isInside(voltage);
      jTextFieldBat.setBackground(isInside ? Color.WHITE : Color.RED);
    }
  }

  @Override // from PutListener
  public void putEvent(MiscPutEvent miscPutEvent) {
    spinnerLabelRimoL.setValue(COMMANDS.get(miscPutEvent.resetRimoL));
    spinnerLabelRimoR.setValue(COMMANDS.get(miscPutEvent.resetRimoR));
    spinnerLabelLinmot.setValue(COMMANDS.get(miscPutEvent.resetLinmot));
    spinnerLabelSteer.setValue(COMMANDS.get(miscPutEvent.resetSteer));
    spinnerLabelLed.setValue(LEDCONTROL.get(miscPutEvent.ledControl));
  }

  @Override // from PutProvider
  public Optional<MiscPutEvent> putEvent() {
    MiscPutEvent miscPutEvent = new MiscPutEvent();
    miscPutEvent.resetRimoL = spinnerLabelRimoL.getValue().getByte();
    miscPutEvent.resetRimoR = spinnerLabelRimoR.getValue().getByte();
    miscPutEvent.resetLinmot = spinnerLabelLinmot.getValue().getByte();
    miscPutEvent.resetSteer = spinnerLabelSteer.getValue().getByte();
    miscPutEvent.ledControl = spinnerLabelLed.getValue().getByte();
    return Optional.of(miscPutEvent);
  }
}
