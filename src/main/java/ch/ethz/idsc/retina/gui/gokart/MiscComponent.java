// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.swing.JTextField;
import javax.swing.JToolBar;

import ch.ethz.idsc.retina.dev.misc.MiscGetEvent;
import ch.ethz.idsc.retina.dev.misc.MiscGetListener;
import ch.ethz.idsc.retina.dev.misc.MiscPutEvent;
import ch.ethz.idsc.retina.dev.misc.MiscPutProvider;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.retina.util.data.Word;
import ch.ethz.idsc.retina.util.gui.SpinnerLabel;
import ch.ethz.idsc.tensor.qty.Quantity;

public class MiscComponent extends InterfaceComponent implements MiscGetListener {
  public static final List<Word> COMMANDS = Arrays.asList( //
      Word.createByte("PASSIVE", (byte) 0), //
      Word.createByte("RESET", (byte) 1) //
  );
  public static final List<Word> LEDCONTROL = Arrays.asList( //
      Word.createByte("OFF", (byte) 0), //
      Word.createByte("ON", (byte) 1) //
  );
  private final SpinnerLabel<Word> spinnerLabelRimoL = new SpinnerLabel<>();
  private final SpinnerLabel<Word> spinnerLabelRimoR = new SpinnerLabel<>();
  private final SpinnerLabel<Word> spinnerLabelLinmot = new SpinnerLabel<>();
  private final SpinnerLabel<Word> spinnerLabelSteer = new SpinnerLabel<>();
  private final SpinnerLabel<Word> spinnerLabelLed = new SpinnerLabel<>();
  private final JTextField jTextFieldEmg;
  private final JTextField jTextFieldBat;

  public MiscComponent() {
    {
      JToolBar jToolBar = createRow("resetRimoL");
      spinnerLabelRimoL.setList(COMMANDS);
      spinnerLabelRimoL.setValueSafe(COMMANDS.get(0));
      spinnerLabelRimoL.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    {
      JToolBar jToolBar = createRow("resetRimoR");
      spinnerLabelRimoR.setList(COMMANDS);
      spinnerLabelRimoR.setValueSafe(COMMANDS.get(0));
      spinnerLabelRimoR.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    {
      JToolBar jToolBar = createRow("resetLinmot");
      spinnerLabelLinmot.setList(COMMANDS);
      spinnerLabelLinmot.setValueSafe(COMMANDS.get(0));
      spinnerLabelLinmot.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    {
      JToolBar jToolBar = createRow("resetSteer");
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
      jTextFieldEmg = createReading("emergency");
      jTextFieldBat = createReading("battery");
    }
  }

  @Override
  public void miscGet(MiscGetEvent miscGetEvent) {
    // jTextFieldEmg.setText("" + miscGetEvent.emergency);
    {
      jTextFieldEmg.setText("" + miscGetEvent.isEmergency());
      Color color = miscGetEvent.isEmergency() ? Color.RED : Color.WHITE;
      jTextFieldEmg.setBackground(color);
    }
    {
      jTextFieldBat.setText(Quantity.of(miscGetEvent.steerBatteryVoltage(), "[V]").toString());
      double value = miscGetEvent.steerBatteryVoltage();
      Color color = value < 11 ? Color.RED : Color.WHITE;
      jTextFieldBat.setBackground(color);
    }
  }

  public final MiscPutProvider miscPutProvider = new MiscPutProvider() {
    @Override
    public ProviderRank getProviderRank() {
      return ProviderRank.TESTING;
    }

    @Override
    public Optional<MiscPutEvent> getPutEvent() {
      MiscPutEvent miscPutEvent = new MiscPutEvent();
      miscPutEvent.resetRimoL = spinnerLabelRimoL.getValue().getByte();
      miscPutEvent.resetRimoR = spinnerLabelRimoR.getValue().getByte();
      miscPutEvent.resetLinmot = spinnerLabelLinmot.getValue().getByte();
      miscPutEvent.resetSteer = spinnerLabelSteer.getValue().getByte();
      miscPutEvent.ledControl = spinnerLabelLed.getValue().getByte();
      return Optional.of(miscPutEvent);
    }
  };
}
