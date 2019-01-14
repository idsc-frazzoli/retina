// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;

public class AutoboxTestingModule extends AbstractModule {
  private final List<AutoboxTestingComponent<?, ?>> list = new LinkedList<>();
  private final JTabbedPane jTabbedPane = new JTabbedPane();
  private final JFrame jFrame = new JFrame("Monitor and Testing");
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());

  @Override
  protected void first() throws Exception {
    addTab(new LinmotComponent());
    addTab(new MiscComponent());
    addTab(new SteerComponent());
    addTab(new RimoComponent());
    // ---
    jTabbedPane.setSelectedIndex(0);
    // ---
    jFrame.setContentPane(jTabbedPane);
    jFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent windowEvent) {
        private_windowClosed();
      }
    });
    windowConfiguration.attach(getClass(), jFrame);
    jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    jFrame.setVisible(true);
  }

  private void private_windowClosed() {
    list.forEach(AutoboxTestingComponent::stop);
  }

  @Override
  protected void last() {
    jFrame.setVisible(false);
    jFrame.dispose();
  }

  private void addTab(AutoboxTestingComponent<?, ?> autoboxTestingComponent) {
    autoboxTestingComponent.start();
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
