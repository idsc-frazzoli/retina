// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.dev.linmot.LinmotSocket;
import ch.ethz.idsc.retina.dev.misc.MiscSocket;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.dev.steer.SteerSocket;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.retina.sys.AppCustomization;
import ch.ethz.idsc.retina.util.gui.WindowConfiguration;

public class AutoboxTestingModule extends AbstractModule {
  private final List<AutoboxTestingComponent<?, ?>> list = new LinkedList<>();
  private final JTabbedPane jTabbedPane = new JTabbedPane();
  private final LinmotComponent linmotComponent = new LinmotComponent();
  private final MiscComponent miscComponent = new MiscComponent();
  private final SteerComponent steerComponent = new SteerComponent();
  private final RimoComponent rimoComponent = new RimoComponent();
  private final JFrame jFrame = new JFrame("Monitor and Testing");
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());

  @Override
  protected void first() throws Exception {
    LinmotSocket.INSTANCE.addGetListener(linmotComponent);
    LinmotSocket.INSTANCE.addPutListener(linmotComponent);
    LinmotSocket.INSTANCE.addPutProvider(linmotComponent);
    // ---
    LinmotSocket.INSTANCE.addGetListener(linmotComponent.linmotInitButton);
    LinmotSocket.INSTANCE.addPutListener(linmotComponent.linmotInitButton);
    addTab(linmotComponent);
    // ---
    MiscSocket.INSTANCE.addGetListener(miscComponent);
    MiscSocket.INSTANCE.addPutListener(miscComponent);
    MiscSocket.INSTANCE.addPutProvider(miscComponent);
    addTab(miscComponent);
    // ---
    SteerSocket.INSTANCE.addGetListener(steerComponent);
    SteerSocket.INSTANCE.addPutListener(steerComponent);
    SteerSocket.INSTANCE.addPutProvider(steerComponent);
    SteerSocket.INSTANCE.addPutListener(steerComponent.steerInitButton);
    addTab(steerComponent);
    // ---
    RimoSocket.INSTANCE.addGetListener(rimoComponent);
    RimoSocket.INSTANCE.addPutListener(rimoComponent);
    RimoSocket.INSTANCE.addPutProvider(rimoComponent);
    addTab(rimoComponent);
    // ---
    jTabbedPane.setSelectedIndex(0);
    // ---
    jFrame.setContentPane(jTabbedPane);
    jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        LinmotSocket.INSTANCE.removeGetListener(linmotComponent);
        LinmotSocket.INSTANCE.removePutListener(linmotComponent);
        LinmotSocket.INSTANCE.removePutProvider(linmotComponent);
        // ---
        LinmotSocket.INSTANCE.removeGetListener(linmotComponent.linmotInitButton);
        LinmotSocket.INSTANCE.removePutListener(linmotComponent.linmotInitButton);
        // ---
        MiscSocket.INSTANCE.removeGetListener(miscComponent);
        MiscSocket.INSTANCE.removePutListener(miscComponent);
        MiscSocket.INSTANCE.removePutProvider(miscComponent);
        // ---
        SteerSocket.INSTANCE.removeGetListener(steerComponent);
        SteerSocket.INSTANCE.removePutListener(steerComponent);
        SteerSocket.INSTANCE.removePutProvider(steerComponent);
        // ---
        SteerSocket.INSTANCE.removePutListener(steerComponent.steerInitButton);
        // ---
        RimoSocket.INSTANCE.removeGetListener(rimoComponent);
        RimoSocket.INSTANCE.removePutListener(rimoComponent);
        RimoSocket.INSTANCE.removePutProvider(rimoComponent);
        System.out.println("removed listeners and providers");
      }
    });
    windowConfiguration.attach(getClass(), jFrame);
    jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    jFrame.setVisible(true);
  }

  @Override
  protected void last() {
    jFrame.setVisible(false);
    jFrame.dispose();
  }

  private void addTab(AutoboxTestingComponent<?, ?> autoboxTestingComponent) {
    list.add(autoboxTestingComponent);
    String string = autoboxTestingComponent.getClass().getSimpleName();
    string = string.substring(0, string.length() - 9);
    jTabbedPane.addTab(string, autoboxTestingComponent.getScrollPane());
  }

  public static void standalone() throws Exception {
    AutoboxTestingModule autoboxTestingModule = new AutoboxTestingModule();
    autoboxTestingModule.first();
    autoboxTestingModule.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  public static void main(String[] args) throws Exception {
    standalone();
  }
}
