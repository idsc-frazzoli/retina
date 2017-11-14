// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.dev.linmot.LinmotGetEvent;
import ch.ethz.idsc.retina.dev.linmot.LinmotGetListener;
import ch.ethz.idsc.retina.dev.linmot.LinmotSocket;
import ch.ethz.idsc.retina.dev.misc.MiscGetEvent;
import ch.ethz.idsc.retina.dev.misc.MiscGetListener;
import ch.ethz.idsc.retina.dev.misc.MiscSocket;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmClient;
import ch.ethz.idsc.retina.sys.AbstractClockedModule;
import ch.ethz.idsc.retina.sys.AppCustomization;
import ch.ethz.idsc.retina.util.gui.SpinnerLabel;
import ch.ethz.idsc.retina.util.gui.WindowConfiguration;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public abstract class JoystickAbstractModule extends AbstractClockedModule {
  private final JFrame jFrame = new JFrame(getClass().getSimpleName());
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());
  public final LinmotInitButton linmotInitButton = new LinmotInitButton();
  public final SteerInitButton steerInitButton = new SteerInitButton();
  private HmiAbstractJoystick joystickInstance;
  private ToolbarsComponent toolbarsComponent = new ToolbarsComponent();
  private JTextField jTextFieldJoystickLast;
  private JTextField jTextFieldVoltage;
  private JTextField jTextFieldTemperature;
  private final MiscGetListener miscGetListener = new MiscGetListener() {
    @Override
    public void getEvent(MiscGetEvent miscGetEvent) {
      jTextFieldVoltage.setText("" + miscGetEvent.getSteerBatteryVoltage());
    }
  };
  private final LinmotGetListener linmotGetListener = new LinmotGetListener() {
    @Override
    public void getEvent(LinmotGetEvent linmotGetEvent) {
      jTextFieldTemperature.setText("" + linmotGetEvent.getWindingTemperatureMax());
    }
  };

  protected abstract HmiAbstractJoystick createJoystick();

  @Override
  protected final void first() throws Exception {
    joystickInstance = createJoystick();
    JoystickLcmClient joystickLcmClient = JoystickLcmClient.any();
    joystickLcmClient.addListener(joystickInstance);
    // ---
    {
      JToolBar jToolBar = toolbarsComponent.createRow("Linmot brake");
      jToolBar.add(linmotInitButton.getComponent());
    }
    {
      JToolBar jToolBar = toolbarsComponent.createRow("Steer");
      jToolBar.add(steerInitButton.getComponent());
    }
    {
      jTextFieldVoltage = toolbarsComponent.createReading("Steer voltage");
    }
    {
      jTextFieldTemperature = toolbarsComponent.createReading("Linmot temp.");
    }
    toolbarsComponent.addSeparator();
    {
      jTextFieldJoystickLast = toolbarsComponent.createReading("Joystick");
    }
    {
      JToolBar jToolBar = toolbarsComponent.createRow("max speed");
      SpinnerLabel<Scalar> spinnerLabel = new SpinnerLabel<>();
      spinnerLabel.setArray(Quantity.of(50, "rad*s^-1"));
      spinnerLabel.setValueSafe(joystickInstance.getSpeedLimit());
      spinnerLabel.addSpinnerListener(i -> joystickInstance.setSpeedLimit(i));
      spinnerLabel.addToComponentReduced(jToolBar, new Dimension(70, 28), "max speed limit");
    }
    // ---
    joystickLcmClient.startSubscriptions();
    // ---
    RimoSocket.INSTANCE.addPutProvider(joystickInstance.getRimoPutProvider());
    LinmotSocket.INSTANCE.addPutProvider(joystickInstance.linmotPutProvider);
    LinmotSocket.INSTANCE.addPutListener(joystickInstance.linmotPutListener);
    LinmotSocket.INSTANCE.addGetListener(joystickInstance.linmotGetListener);
    LinmotSocket.INSTANCE.addGetListener(linmotGetListener);
    LinmotSocket.INSTANCE.addAll(linmotInitButton);
    SteerSocket.INSTANCE.addPutProvider(joystickInstance.steerPutProvider);
    SteerSocket.INSTANCE.addAll(steerInitButton);
    MiscSocket.INSTANCE.addGetListener(miscGetListener);
    // ---
    jFrame.setContentPane(toolbarsComponent.getScrollPane());
    jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        RimoSocket.INSTANCE.removePutProvider(joystickInstance.getRimoPutProvider());
        LinmotSocket.INSTANCE.removePutProvider(joystickInstance.linmotPutProvider);
        LinmotSocket.INSTANCE.removePutListener(joystickInstance.linmotPutListener);
        LinmotSocket.INSTANCE.removeGetListener(joystickInstance.linmotGetListener);
        LinmotSocket.INSTANCE.removeGetListener(linmotGetListener);
        LinmotSocket.INSTANCE.removeAll(linmotInitButton);
        SteerSocket.INSTANCE.removePutProvider(joystickInstance.steerPutProvider);
        SteerSocket.INSTANCE.removeAll(steerInitButton);
        MiscSocket.INSTANCE.removeGetListener(miscGetListener);
        // ---
        System.out.println("removed listeners and providers");
        joystickLcmClient.stopSubscriptions();
        joystickLcmClient.removeListener(joystickInstance);
      }
    });
    windowConfiguration.attach(getClass(), jFrame);
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setVisible(true);
  }

  @Override
  protected final void last() {
    jFrame.setVisible(false);
    jFrame.dispose();
  }

  @Override
  protected void runAlgo() {
    boolean hasJoystick = joystickInstance.hasJoystick();
    jTextFieldJoystickLast.setText("" + hasJoystick);
    jTextFieldJoystickLast.setBackground(hasJoystick ? Color.GREEN : Color.RED);
  }

  @Override
  protected double getPeriod() {
    return 0.05;
  }
}
