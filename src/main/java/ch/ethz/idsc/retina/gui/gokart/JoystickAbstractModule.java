// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.dev.linmot.LinmotSocket;
import ch.ethz.idsc.retina.dev.misc.MiscSocket;
import ch.ethz.idsc.retina.dev.rimo.RimoPutTire;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.lcm.joystick.JoystickLcmClient;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.retina.util.gui.SpinnerLabel;

public abstract class JoystickAbstractModule extends AbstractModule {
  private final JFrame jFrame = new JFrame(getClass().getSimpleName());
  public final LinmotInitButton linmotInitButton = new LinmotInitButton();
  public final SteerInitButton steerInitButton = new SteerInitButton();

  protected abstract HmiAbstractJoystick createJoystick();

  ToolbarsComponent toolbarsComponent = new ToolbarsComponent();

  @Override
  protected final void first() throws Exception {
    final HmiAbstractJoystick joystickInstance = createJoystick();
    JoystickLcmClient.INSTANCE.addListener(joystickInstance);
    // ---
    RimoSocket.INSTANCE.addPutProvider(joystickInstance.getRimoPutProvider());
    LinmotSocket.INSTANCE.addPutProvider(joystickInstance.linmotPutProvider);
    LinmotSocket.INSTANCE.addPutListener(joystickInstance.linmotPutListener);
    LinmotSocket.INSTANCE.addGetListener(joystickInstance.linmotGetListener);
    LinmotSocket.INSTANCE.addPutListener(linmotInitButton);
    SteerSocket.INSTANCE.addPutProvider(joystickInstance.steerPutProvider);
    SteerSocket.INSTANCE.addPutListener(steerInitButton);
    MiscSocket.INSTANCE.addPutProvider(joystickInstance.miscPutProvider);
    // ---
    {
      JToolBar jToolBar = toolbarsComponent.createRow("linmot");
      jToolBar.add(linmotInitButton.getComponent());
    }
    {
      JToolBar jToolBar = toolbarsComponent.createRow("steer");
      jToolBar.add(steerInitButton.getComponent());
    }
    toolbarsComponent.addSeparator();
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
    jFrame.setBounds(200, 200, 320, 270);
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        RimoSocket.INSTANCE.removePutProvider(joystickInstance.getRimoPutProvider());
        LinmotSocket.INSTANCE.removePutProvider(joystickInstance.linmotPutProvider);
        LinmotSocket.INSTANCE.removePutListener(joystickInstance.linmotPutListener);
        LinmotSocket.INSTANCE.removeGetListener(joystickInstance.linmotGetListener);
        LinmotSocket.INSTANCE.removePutListener(linmotInitButton);
        SteerSocket.INSTANCE.removePutProvider(joystickInstance.steerPutProvider);
        SteerSocket.INSTANCE.removePutListener(steerInitButton);
        MiscSocket.INSTANCE.removePutProvider(joystickInstance.miscPutProvider);
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
}
