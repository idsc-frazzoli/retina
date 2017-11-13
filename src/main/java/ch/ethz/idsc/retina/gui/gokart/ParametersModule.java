// code by jph
package ch.ethz.idsc.retina.gui.gokart;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.dev.rimo.TorqueConfig;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.gui.gokart.top.ChassisGeometry;
import ch.ethz.idsc.retina.gui.gokart.top.SensorsConfig;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.retina.sys.AppCustomization;
import ch.ethz.idsc.retina.util.gui.WindowConfiguration;

public class ParametersModule extends AbstractModule {
  private final JTabbedPane jTabbedPane = new JTabbedPane();
  private final JFrame jFrame = new JFrame("Parameters");
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());

  @Override
  protected void first() throws Exception {
    addTab(ChassisGeometry.GLOBAL);
    addTab(SensorsConfig.GLOBAL);
    addTab(SteerConfig.GLOBAL);
    addTab(TorqueConfig.GLOBAL);
    jFrame.setContentPane(jTabbedPane);
    windowConfiguration.attach(getClass(), jFrame);
    jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    jFrame.setVisible(true);
  }

  @Override
  protected void last() {
    jFrame.setVisible(false);
    jFrame.dispose();
  }

  private void addTab(Object object) {
    ParametersComponent propertiesComponent = new ParametersComponent(object);
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
