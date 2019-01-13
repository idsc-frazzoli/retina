// code by jph
package ch.ethz.idsc.gokart.gui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.GuiConfig;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;
import ch.ethz.idsc.tensor.io.TensorProperties;

/** ParametersModule is a graphical user interface to configure all constant
 * quantities that are critical for the safety and performance of the gokart
 * platform: sensor positioning, limits on actuators, ...
 * 
 * In most cases, the modification of a parameter value in the gui takes effect
 * immediately, i.e. does not require the restart of the software. */
public class ParametersModule extends AbstractModule {
  private final JTabbedPane jTabbedPane = new JTabbedPane();
  private final JFrame jFrame = new JFrame("Parameters");
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());

  @Override // from AbstractModule
  protected void first() throws Exception {
    ParametersHelper.OBJECTS.forEach(this::addTab);
    // only classes that other classes do not extend from
    jFrame.setContentPane(jTabbedPane);
    windowConfiguration.attach(getClass(), jFrame);
    jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    jFrame.setVisible(true);
  }

  @Override // from AbstractModule
  protected void last() {
    jFrame.setVisible(false);
    jFrame.dispose();
  }

  private void addTab(Object object) {
    // only include config class with configurable parameters
    if (0 < TensorProperties.wrap(object).fields().count()) {
      ParametersComponent propertiesComponent = new ParametersComponent(object);
      String title = object.getClass().getSimpleName();
      title = title.endsWith("Config") //
          ? title.substring(0, title.length() - 6)
          : title;
      jTabbedPane.addTab(title, propertiesComponent.getScrollPane());
      {
        // change tab component to modify display size
        int count = jTabbedPane.getTabCount() - 1;
        JLabel jLabel = GuiConfig.GLOBAL.createSubLabel(title);
        jTabbedPane.setTabComponentAt(count, jLabel);
      }
    }
  }

  /***************************************************/
  public static void standalone() throws Exception {
    ParametersModule parametersModule = new ParametersModule();
    parametersModule.first();
    parametersModule.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  public static void main(String[] args) throws Exception {
    standalone();
  }
}
