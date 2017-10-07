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

import ch.ethz.idsc.retina.dev.linmot.LinmotSocket;
import ch.ethz.idsc.retina.dev.misc.MiscGetEvent;
import ch.ethz.idsc.retina.dev.misc.MiscGetListener;
import ch.ethz.idsc.retina.dev.rimo.RimoPutTire;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmClient;
import ch.ethz.idsc.retina.sys.AbstractClockedModule;
import ch.ethz.idsc.retina.util.gui.SpinnerLabel;

public abstract class JoystickAbstractModule extends AbstractClockedModule implements MiscGetListener {
  private final JFrame jFrame = new JFrame(getClass().getSimpleName());
  public final LinmotInitButton linmotInitButton = new LinmotInitButton();
  public final SteerInitButton steerInitButton = new SteerInitButton();
  private HmiAbstractJoystick joystickInstance;
  private ToolbarsComponent toolbarsComponent = new ToolbarsComponent();
  private JTextField jTextFieldJoystickLast;
  private JTextField jTextFieldVoltage;

  protected abstract HmiAbstractJoystick createJoystick();

  @Override
  protected final void first() throws Exception {
    joystickInstance = createJoystick();
    JoystickLcmClient.INSTANCE.addListener(joystickInstance);
    // ---
    RimoSocket.INSTANCE.addPutProvider(joystickInstance.getRimoPutProvider());
    LinmotSocket.INSTANCE.addPutProvider(joystickInstance.linmotPutProvider);
    LinmotSocket.INSTANCE.addPutListener(joystickInstance.linmotPutListener);
    LinmotSocket.INSTANCE.addGetListener(joystickInstance.linmotGetListener);
    LinmotSocket.INSTANCE.addAll(linmotInitButton);
    SteerSocket.INSTANCE.addPutProvider(joystickInstance.steerPutProvider);
    SteerSocket.INSTANCE.addAll(steerInitButton);
    // MiscSocket.INSTANCE.addPutProvider(joystickInstance.miscPutProvider);
    // ---
    {
      JToolBar jToolBar = toolbarsComponent.createRow("linmot");
      jToolBar.add(linmotInitButton.getComponent());
    }
    {
      JToolBar jToolBar = toolbarsComponent.createRow("steer");
      jToolBar.add(steerInitButton.getComponent());
    }
    {
      jTextFieldVoltage = toolbarsComponent.createReading("Steer voltage");
    }
    toolbarsComponent.addSeparator();
    {
      jTextFieldJoystickLast = toolbarsComponent.createReading("Joystick");
    }
    {
      JToolBar jToolBar = toolbarsComponent.createRow("max speed");
      SpinnerLabel<Integer> spinnerLabel = new SpinnerLabel<>();
      spinnerLabel.setArray(0, 500, 1000, 2000, 4000, (int) RimoPutTire.MAX_SPEED);
      spinnerLabel.setValueSafe(joystickInstance.getSpeedLimit());
      spinnerLabel.addSpinnerListener(i -> joystickInstance.setSpeedLimit(i));
      spinnerLabel.addToComponentReduced(jToolBar, new Dimension(70, 28), "max speed limit");
    }
    // ---
    jFrame.setContentPane(toolbarsComponent.getScrollPane());
    jFrame.setBounds(200, 200, 380, 270);
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        RimoSocket.INSTANCE.removePutProvider(joystickInstance.getRimoPutProvider());
        LinmotSocket.INSTANCE.removePutProvider(joystickInstance.linmotPutProvider);
        LinmotSocket.INSTANCE.removePutListener(joystickInstance.linmotPutListener);
        LinmotSocket.INSTANCE.removeGetListener(joystickInstance.linmotGetListener);
        LinmotSocket.INSTANCE.removeAll(linmotInitButton);
        SteerSocket.INSTANCE.removePutProvider(joystickInstance.steerPutProvider);
        SteerSocket.INSTANCE.removeAll(steerInitButton);
        // MiscSocket.INSTANCE.removePutProvider(joystickInstance.miscPutProvider);
        // ---
        System.out.println("removed listeners and providers");
        JoystickLcmClient.INSTANCE.removeListener(joystickInstance);
      }
    });
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

  @Override
  public final void getEvent(MiscGetEvent miscGetEvent) {
    jTextFieldVoltage.setText("" + miscGetEvent.getSteerBatteryVoltage());
  }
}
