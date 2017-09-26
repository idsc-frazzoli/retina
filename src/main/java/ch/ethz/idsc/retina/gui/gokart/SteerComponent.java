// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Optional;

import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import ch.ethz.idsc.retina.dev.steer.SteerAngleTracker;
import ch.ethz.idsc.retina.dev.steer.SteerGetEvent;
import ch.ethz.idsc.retina.dev.steer.SteerGetListener;
import ch.ethz.idsc.retina.dev.steer.SteerPutEvent;
import ch.ethz.idsc.retina.dev.steer.SteerPutProvider;
import ch.ethz.idsc.retina.dev.zhkart.ProviderRank;
import ch.ethz.idsc.retina.util.data.Word;
import ch.ethz.idsc.retina.util.gui.SliderExt;
import ch.ethz.idsc.retina.util.gui.SpinnerLabel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ColorFormat;

class SteerComponent extends AutoboxTestingComponent implements SteerGetListener {
  public static final int AMP = 1000;
  public static final double FACTOR = 0.5;
  // ---
  private final SpinnerLabel<Word> spinnerLabelLw = new SpinnerLabel<>();
  private final SliderExt sliderExtTorque;
  private final JTextField[] jTextField = new JTextField[11];
  private final SteerAngleTracker steerAngleTracker = new SteerAngleTracker();

  public SteerComponent() {
    {
      JToolBar jToolBar = createRow("command");
      spinnerLabelLw.setList(SteerPutEvent.COMMANDS);
      spinnerLabelLw.setValueSafe(SteerPutEvent.CMD_ON);
      spinnerLabelLw.addToComponent(jToolBar, new Dimension(200, 20), "");
    }
    { // command speed
      JToolBar jToolBar = createRow("torque");
      sliderExtTorque = SliderExt.wrap(new JSlider(-AMP, AMP, 0)); // values are divided by 1000
      sliderExtTorque.physics = SteerComponent::giveTorque;
      sliderExtTorque.addToComponent(jToolBar);
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
    double angle = steerAngleTracker.getSteeringAngle(steerGetEvent);
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
    return RealScalar.of(value * FACTOR / AMP);
  }

  public final SteerPutProvider steerPutProvider = new SteerPutProvider() {
    @Override
    public ProviderRank getProviderRank() {
      return ProviderRank.TESTING;
    }

    @Override
    public Optional<SteerPutEvent> getPutEvent() {
      return Optional.of(new SteerPutEvent(spinnerLabelLw.getValue(), //
          giveTorque(sliderExtTorque.jSlider.getValue()).number().doubleValue()));
    }
  };
}
