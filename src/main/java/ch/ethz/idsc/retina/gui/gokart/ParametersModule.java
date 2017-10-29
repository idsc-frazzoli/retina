// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.gui.gokart.top.SensorsConfig;
import ch.ethz.idsc.retina.sys.AbstractModule;

public class ParametersModule extends AbstractModule {
  private final JTabbedPane jTabbedPane = new JTabbedPane();
  private final JFrame jFrame = new JFrame("Parameters");

  @Override
  protected void first() throws Exception {
    addTab(SensorsConfig.GLOBAL);
    addTab(SteerConfig.GLOBAL);
    jFrame.setContentPane(jTabbedPane);
    jFrame.setBounds(600, 80, 400, 400);
    jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    jFrame.setVisible(true);
  }

  @Override
  protected void last() {
    jFrame.setVisible(false);
    jFrame.dispose();
  }

  private void addTab(Object object) {
    PropertiesComponent propertiesComponent = new PropertiesComponent(object);
    jTabbedPane.addTab(object.getClass().getSimpleName(), propertiesComponent.getScrollPane());
  }

  public static void standalone() throws Exception {
    ParametersModule parametersModule = new ParametersModule();
    parametersModule.first();
    parametersModule.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  public static void main(String[] args) throws Exception {
    standalone();
  }
}
