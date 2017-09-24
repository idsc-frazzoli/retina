// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.dev.linmot.LinmotSocket;
import ch.ethz.idsc.retina.dev.misc.MiscSocket;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.sys.AbstractModule;

public class AutoboxTestingModule extends AbstractModule {
  private final List<InterfaceComponent> list = new LinkedList<>();
  private final JTabbedPane jTabbedPane = new JTabbedPane();
  private final RimoComponent rimoComponent = new RimoComponent();
  private final LinmotComponent linmotComponent = new LinmotComponent();
  private final SteerComponent steerComponent = new SteerComponent();
  private final MiscComponent miscComponent = new MiscComponent();
  public final JFrame jFrame = new JFrame();

  @Override
  protected void first() throws Exception {
    RimoSocket.INSTANCE.addListener(rimoComponent);
    RimoSocket.INSTANCE.addProvider(rimoComponent.rimoPutProvider);
    addTab(rimoComponent);
    // ---
    LinmotSocket.INSTANCE.addListener(linmotComponent);
    LinmotSocket.INSTANCE.addProvider(linmotComponent.linmotPutProvider);
    addTab(linmotComponent);
    // ---
    SteerSocket.INSTANCE.addListener(steerComponent);
    SteerSocket.INSTANCE.addProvider(steerComponent.steerPutProvider);
    addTab(steerComponent);
    // ---
    MiscSocket.INSTANCE.addListener(miscComponent);
    MiscSocket.INSTANCE.addProvider(miscComponent.miscPutProvider);
    addTab(miscComponent);
    // ---
    jTabbedPane.setSelectedIndex(0);
    // ---
    jFrame.setContentPane(jTabbedPane);
    jFrame.setBounds(100, 80, 500, 700);
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        RimoSocket.INSTANCE.removeListener(rimoComponent);
        RimoSocket.INSTANCE.removeProvider(rimoComponent.rimoPutProvider);
        // ---
        LinmotSocket.INSTANCE.removeListener(linmotComponent);
        LinmotSocket.INSTANCE.removeProvider(linmotComponent.linmotPutProvider);
        // ---
        SteerSocket.INSTANCE.removeListener(steerComponent);
        SteerSocket.INSTANCE.removeProvider(steerComponent.steerPutProvider);
        // ---
        MiscSocket.INSTANCE.removeListener(miscComponent);
        MiscSocket.INSTANCE.removeProvider(miscComponent.miscPutProvider);
        System.out.println("removed listeners and providers");
      }
    });
    jFrame.setVisible(true);
  }

  @Override
  protected void last() {
    jFrame.setVisible(false);
    jFrame.dispose();
  }

  private void addTab(InterfaceComponent interfaceComponent) {
    list.add(interfaceComponent);
    String string = interfaceComponent.getClass().getSimpleName();
    string = string.substring(0, string.length() - 9);
    JPanel jPanel = new JPanel(new BorderLayout());
    jPanel.add(interfaceComponent.getComponent(), BorderLayout.NORTH);
    JScrollPane jScrollPane = new JScrollPane(jPanel);
    jTabbedPane.addTab(string, jScrollPane);
  }
}
