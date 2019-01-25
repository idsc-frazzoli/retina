// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import java.awt.Color;
import java.util.Objects;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import ch.ethz.idsc.gokart.core.fuse.DavisImuTracker;
import ch.ethz.idsc.gokart.core.man.ManualConfig;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.gokart.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.gokart.dev.rimo.RimoGetListener;
import ch.ethz.idsc.gokart.gui.ToolbarsComponent;
import ch.ethz.idsc.gokart.lcm.autobox.LinmotGetLcmClient;
import ch.ethz.idsc.gokart.lcm.autobox.RimoGetLcmClient;
import ch.ethz.idsc.gokart.lcm.imu.Vmu931ImuLcmClient;
import ch.ethz.idsc.gokart.lcm.imu.Vmu931LcmServerModule;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrame;
import ch.ethz.idsc.retina.imu.vmu931.Vmu931ImuFrameListener;
import ch.ethz.idsc.retina.joystick.ManualControlInterface;
import ch.ethz.idsc.retina.joystick.ManualControlProvider;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.retina.util.math.NonSI;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ColorFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.Put;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Clip;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ class AutoboxCompactComponent extends ToolbarsComponent implements StartAndStoppable {
  private static final Clip CLIP_DEG_C = Clip.function( //
      Quantity.of(+20, NonSI.DEGREE_CELSIUS), //
      Quantity.of(100, NonSI.DEGREE_CELSIUS));
  private static final Clip CLIP_GYROZ = Clip.function( //
      Quantity.of(-1, SI.PER_SECOND), //
      Quantity.of(+1, SI.PER_SECOND));
  private static final Clip CLIP_AHEAD = Clip.absoluteOne();
  // ---
  private final RimoGetLcmClient rimoGetLcmClient = new RimoGetLcmClient();
  private final LinmotGetLcmClient linmotGetLcmClient = new LinmotGetLcmClient();
  private final ManualControlProvider manualControlProvider = ManualConfig.GLOBAL.createProvider();
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private final Vmu931ImuLcmClient vmu931ImuLcmClient = new Vmu931ImuLcmClient();
  private final Timer timer = new Timer();
  private final RimoGetListener rimoGetListener = getEvent -> rimoGetEvent = getEvent;
  private final LinmotGetListener linmotGetListener = getEvent -> linmotGetEvent = getEvent;
  private final GokartPoseListener gokartPoseListener = getEvent -> gokartPoseEvent = getEvent;
  private final Vmu931ImuFrameListener vmu931ImuFrameListener = getEvent -> vmu931ImuFrame = getEvent;
  private final LinmotInitButton linmotInitButton = new LinmotInitButton();
  private final MiscResetButton miscResetButton = new MiscResetButton();
  private final SteerInitButton steerInitButton = new SteerInitButton();
  private final JTextField jTF_rimoRatePair;
  private final JTextField jTF_linmotTemp;
  private final JTextField jTF_manualControl;
  private final JTextField jTF_ahead;
  private final JTextField jTF_davis240c;
  private final JTextField jTF_vmu931_acc;
  private final JTextField jTF_vmu931_gyr;
  private final JTextField jTF_localPose;
  private final JButton jButtonAppend = new JButton("pose append");
  private final JTextField jTF_localQual;
  private final Tensor poseList = Tensors.empty();
  // ---
  private GokartPoseEvent gokartPoseEvent;
  private RimoGetEvent rimoGetEvent;
  private LinmotGetEvent linmotGetEvent;
  private Vmu931ImuFrame vmu931ImuFrame;

  public AutoboxCompactComponent() {
    {
      JToolBar jToolBar = createRow("Actuation");
      jToolBar.add(linmotInitButton.getComponent());
      jToolBar.add(miscResetButton.getComponent());
      jToolBar.add(steerInitButton.getComponent());
    }
    jTF_rimoRatePair = createReading("Rimo");
    jTF_linmotTemp = createReading("Linmot");
    jTF_davis240c = createReading("Davis240C");
    jTF_manualControl = createReading("Manual");
    jTF_ahead = createReading("Ahead");
    jTF_vmu931_acc = createReading("Vmu931 acc");
    jTF_vmu931_gyr = createReading("Vmu931 gyr");
    Vmu931LcmServerModule vmu931LcmServerModule = ModuleAuto.INSTANCE.getInstance(Vmu931LcmServerModule.class);
    if (Objects.nonNull(vmu931LcmServerModule)) {
      JToolBar jToolBar = createRow("vmu931 ctrl");
      {
        JButton jButton = new JButton("status");
        jButton.addActionListener(actionEvent -> vmu931LcmServerModule.requestStatus());
        jToolBar.add(jButton);
      }
      {
        JButton jButton = new JButton("self-test");
        jButton.addActionListener(actionEvent -> vmu931LcmServerModule.requestSelftest());
        jToolBar.add(jButton);
      }
      {
        JButton jButton = new JButton("calibration");
        jButton.addActionListener(actionEvent -> vmu931LcmServerModule.requestCalibration());
        jToolBar.add(jButton);
      }
    }
    jTF_localPose = createReading("Pose");
    jTF_localQual = createReading("Pose quality");
    {
      JToolBar jToolBar = createRow("store");
      jButtonAppend.addActionListener(actionEvent -> {
        if (Objects.nonNull(gokartPoseEvent)) {
          Tensor state = gokartPoseEvent.getPose();
          state = GokartPoseHelper.toUnitless(state);
          state.set(Round._2, 0);
          state.set(Round._2, 1);
          state.set(Round._6, 2);
          poseList.append(state);
          try {
            Put.of(HomeDirectory.file("track.mathematica"), poseList);
            Export.of(HomeDirectory.file("track.csv"), poseList);
          } catch (Exception exception) {
            exception.printStackTrace();
          }
        }
      });
      jToolBar.add(jButtonAppend);
    }
  }

  @Override // from StartAndStoppable
  public void start() {
    rimoGetLcmClient.addListener(rimoGetListener);
    rimoGetLcmClient.startSubscriptions();
    linmotGetLcmClient.addListener(linmotGetListener);
    linmotGetLcmClient.startSubscriptions();
    // ---
    manualControlProvider.start();
    // ---
    gokartPoseLcmClient.addListener(gokartPoseListener);
    gokartPoseLcmClient.startSubscriptions();
    // ---
    vmu931ImuLcmClient.addListener(vmu931ImuFrameListener);
    vmu931ImuLcmClient.startSubscriptions();
    // ---
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        {
          linmotInitButton.updateEnabled();
          miscResetButton.updateEnabled();
          steerInitButton.updateEnabled();
          if (Objects.nonNull(rimoGetEvent)) {
            String pair = rimoGetEvent.getAngularRate_Y_pair().map(Round._3).toString();
            jTF_rimoRatePair.setText(pair);
          }
        }
        {
          if (Objects.nonNull(linmotGetEvent)) {
            Scalar temperatureMax = linmotGetEvent.getWindingTemperatureMax();
            Scalar rescaled = CLIP_DEG_C.rescale(temperatureMax);
            Color color = ColorFormat.toColor(ColorDataGradients.TEMPERATURE.apply(rescaled));
            jTF_linmotTemp.setText(temperatureMax.map(Round._1).toString());
            jTF_linmotTemp.setBackground(color);
          }
        }
        {
          Optional<ManualControlInterface> optional = manualControlProvider.getManualControl();
          {
            String string = optional.isPresent() //
                ? optional.get().toString()
                : ToolbarsComponent.UNKNOWN;
            jTF_manualControl.setText(string);
          }
          {
            String string = ToolbarsComponent.UNKNOWN;
            if (optional.isPresent()) {
              ManualControlInterface manualControlInterface = optional.get();
              Scalar aheadAverage = manualControlInterface.getAheadAverage();
              Scalar rescaled = CLIP_AHEAD.rescale(aheadAverage);
              Color color = ColorFormat.toColor(ColorDataGradients.TEMPERATURE.apply(rescaled));
              jTF_ahead.setBackground(color);
              string = aheadAverage.map(Round._4).toString();
            }
            jTF_ahead.setText(string);
          }
        }
        {
          Scalar gyroZ = DavisImuTracker.INSTANCE.getGyroZ();
          Scalar rescaled = CLIP_GYROZ.rescale(gyroZ);
          Color color = ColorFormat.toColor(ColorDataGradients.THERMOMETER.apply(rescaled));
          String text = "#=" + DavisImuTracker.INSTANCE.getFramecount();
          jTF_davis240c.setText(text + " " + gyroZ);
          jTF_davis240c.setBackground(color);
        }
        { // pose coordinates
          String string = Objects.nonNull(gokartPoseEvent) //
              ? gokartPoseEvent.getPose().map(Round._3).toString()
              : ToolbarsComponent.UNKNOWN;
          jTF_localPose.setText(string);
        }
        {
          if (Objects.nonNull(vmu931ImuFrame)) {
            jTF_vmu931_acc.setText(vmu931ImuFrame.acceleration().map(Round._3).toString());
            jTF_vmu931_gyr.setText(vmu931ImuFrame.gyroscope().map(Round._3).toString());
          }
        }
        if (Objects.isNull(gokartPoseEvent)) { // pose quality
          jTF_localQual.setText(ToolbarsComponent.UNKNOWN);
          jTF_localQual.setBackground(null);
        } else {
          String string = gokartPoseEvent.getQuality().map(Round._3).toString();
          jTF_localQual.setText(string);
          Color color = ColorFormat.toColor(ColorDataGradients.MINT.apply(RealScalar.ONE.subtract(gokartPoseEvent.getQuality())));
          jTF_localQual.setBackground(color);
        }
      }
    }, 100, 50); // update rate 20[hz]
  }

  @Override // from StartAndStoppable
  public void stop() {
    timer.cancel();
    manualControlProvider.stop();
    linmotGetLcmClient.stopSubscriptions();
    rimoGetLcmClient.stopSubscriptions();
    gokartPoseLcmClient.stopSubscriptions();
    vmu931ImuLcmClient.stopSubscriptions();
  }
}
