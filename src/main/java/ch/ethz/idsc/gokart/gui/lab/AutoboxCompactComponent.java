// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JToolBar;

import ch.ethz.idsc.gokart.core.joy.JoystickConfig;
import ch.ethz.idsc.gokart.core.pos.GokartPoseEvent;
import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLcmClient;
import ch.ethz.idsc.gokart.core.pos.GokartPoseListener;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.ToolbarsComponent;
import ch.ethz.idsc.gokart.lcm.autobox.RimoGetLcmClient;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrame;
import ch.ethz.idsc.retina.dev.davis.data.DavisImuFrameListener;
import ch.ethz.idsc.retina.dev.joystick.GokartJoystickInterface;
import ch.ethz.idsc.retina.dev.joystick.JoystickEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetEvent;
import ch.ethz.idsc.retina.dev.rimo.RimoGetListener;
import ch.ethz.idsc.retina.lcm.davis.DavisImuLcmClient;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmProvider;
import ch.ethz.idsc.retina.util.StartAndStoppable;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ColorFormat;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.Put;
import ch.ethz.idsc.tensor.sca.Round;

public class AutoboxCompactComponent extends ToolbarsComponent implements StartAndStoppable, DavisImuFrameListener {
  private final RimoGetLcmClient rimoGetLcmClient = new RimoGetLcmClient();
  private final JoystickLcmProvider joystickLcmProvider = JoystickConfig.GLOBAL.createProvider();
  private final DavisImuLcmClient davisImuLcmClient = new DavisImuLcmClient(GokartLcmChannel.DAVIS_OVERVIEW);
  private final GokartPoseLcmClient gokartPoseLcmClient = new GokartPoseLcmClient();
  private final Timer timer = new Timer();
  private final RimoGetListener rimoGetListener = getEvent -> rimoGetEvent = getEvent;
  private final GokartPoseListener gokartPoseListener = getEvent -> gokartPoseEvent = getEvent;
  private int imuFrame_count = 0;
  private GokartPoseEvent gokartPoseEvent;
  private RimoGetEvent rimoGetEvent;
  private final LinmotInitButton linmotInitButton = new LinmotInitButton();
  private final MiscResetButton miscResetButton = new MiscResetButton();
  private final SteerInitButton steerInitButton = new SteerInitButton();
  private final JTextField jTF_rimoRatePair;
  private final JTextField jTF_joystick;
  private final JTextField jTF_joystickAhead;
  private final JTextField jTF_davis240c;
  private final JTextField jTF_localPose;
  private final JButton jButtonAppend;
  private final JTextField jTF_localQual;
  private final Tensor poseList = Tensors.empty();

  public AutoboxCompactComponent() {
    {
      JToolBar jToolBar = createRow("Linmot");
      jToolBar.add(linmotInitButton.getComponent());
    }
    {
      JToolBar jToolBar = createRow("Misc");
      jToolBar.add(miscResetButton.getComponent());
    }
    {
      JToolBar jToolBar = createRow("Steer");
      jToolBar.add(steerInitButton.getComponent());
    }
    jTF_rimoRatePair = createReading("Rimo");
    jTF_davis240c = createReading("Davis240C");
    jTF_joystick = createReading("Joystick");
    jTF_joystickAhead = createReading("Ahead");
    jTF_localPose = createReading("Pose");
    jTF_localQual = createReading("Pose quality");
    {
      JToolBar jToolBar = createRow("store");
      jButtonAppend = new JButton("pose append");
      jButtonAppend.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          if (Objects.nonNull(gokartPoseEvent)) {
            Tensor state = gokartPoseEvent.getPose();
            state = GokartPoseHelper.toUnitless(state);
            state.set(Round._2, 0);
            state.set(Round._2, 1);
            state.set(Round._6, 2);
            poseList.append(state);
            try {
              Put.of(UserHome.file("track.mathematica"), poseList);
              Export.of(UserHome.file("track.csv"), poseList);
            } catch (Exception exception) {
              exception.printStackTrace();
            }
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
    // ---
    davisImuLcmClient.addListener(this);
    davisImuLcmClient.startSubscriptions();
    // ---
    joystickLcmProvider.startSubscriptions();
    // ---
    gokartPoseLcmClient.addListener(gokartPoseListener);
    gokartPoseLcmClient.startSubscriptions();
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
          Optional<JoystickEvent> optional = joystickLcmProvider.getJoystick();
          String string = optional.isPresent() ? optional.get().toString() : ToolbarsComponent.UNKNOWN;
          jTF_joystick.setText(string);
        }
        {
          Optional<JoystickEvent> optional = joystickLcmProvider.getJoystick();
          String string = ToolbarsComponent.UNKNOWN;
          if (optional.isPresent()) {
            GokartJoystickInterface gokartJoystickInterface = (GokartJoystickInterface) optional.get();
            string = gokartJoystickInterface.getAheadAverage().map(Round._5).toString();
          }
          jTF_joystickAhead.setText(string);
        }
        jTF_davis240c.setText("#=" + imuFrame_count);
        { // pose coordinates
          String string = Objects.nonNull(gokartPoseEvent) //
              ? gokartPoseEvent.getPose().map(Round._3).toString()
              : ToolbarsComponent.UNKNOWN;
          jTF_localPose.setText(string);
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
    joystickLcmProvider.stopSubscriptions();
    davisImuLcmClient.stopSubscriptions();
    rimoGetLcmClient.stopSubscriptions();
    gokartPoseLcmClient.stopSubscriptions();
  }

  @Override // from DavisImuFrameListener
  public void imuFrame(DavisImuFrame davisImuFrame) {
    ++imuFrame_count;
  }
}
