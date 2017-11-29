// code by jph
package ch.ethz.idsc.retina.gui.gokart;

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
  private final RimoComponent rimoComponent = new RimoComponent();
  private final LinmotComponent linmotComponent = new LinmotComponent();
  private final SteerComponent steerComponent = new SteerComponent();
  private final MiscComponent miscComponent = new MiscComponent();
  private final JFrame jFrame = new JFrame("Monitor and Testing");
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());

  @Override
  protected void first() throws Exception {
    LinmotSocket.INSTANCE.addAll(linmotComponent);
    LinmotSocket.INSTANCE.addAll(linmotComponent.linmotInitButton);
    addTab(linmotComponent);
    // ---
    SteerSocket.INSTANCE.addAll(steerComponent);
    SteerSocket.INSTANCE.addPutListener(steerComponent.steerInitButton);
    addTab(steerComponent);
    // ---
    MiscSocket.INSTANCE.addAll(miscComponent);
    addTab(miscComponent);
    // ---
    RimoSocket.INSTANCE.addAll(rimoComponent);
    addTab(rimoComponent);
    // ---
    jTabbedPane.setSelectedIndex(0);
    // ---
    jFrame.setContentPane(jTabbedPane);
    jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        RimoSocket.INSTANCE.removeAll(rimoComponent);
        LinmotSocket.INSTANCE.removeAll(linmotComponent);
        LinmotSocket.INSTANCE.removeAll(linmotComponent.linmotInitButton);
        SteerSocket.INSTANCE.removeAll(steerComponent);
        SteerSocket.INSTANCE.removePutListener(steerComponent.steerInitButton);
        MiscSocket.INSTANCE.removeAll(miscComponent);
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
