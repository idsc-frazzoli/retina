// code by jph
package ch.ethz.idsc.gokart.gui;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import ch.ethz.idsc.demo.mg.slam.config.SlamCoreConfig;
import ch.ethz.idsc.demo.mg.slam.config.SlamPrcConfig;
import ch.ethz.idsc.gokart.core.fuse.SafetyConfig;
import ch.ethz.idsc.gokart.core.joy.JoystickConfig;
import ch.ethz.idsc.gokart.core.map.MappingConfig;
import ch.ethz.idsc.gokart.core.perc.ClusterConfig;
import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.core.pure.PursuitConfig;
import ch.ethz.idsc.gokart.core.pure.TrajectoryConfig;
import ch.ethz.idsc.gokart.gui.top.ChassisGeometry;
import ch.ethz.idsc.gokart.gui.top.SensorsConfig;
import ch.ethz.idsc.retina.dev.linmot.LinmotConfig;
import ch.ethz.idsc.retina.dev.rimo.RimoConfig;
import ch.ethz.idsc.retina.dev.steer.SteerConfig;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.retina.sys.AppCustomization;
import ch.ethz.idsc.retina.util.gui.WindowConfiguration;

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
    addTab(ChassisGeometry.GLOBAL);
    addTab(SensorsConfig.GLOBAL);
    addTab(LinmotConfig.GLOBAL);
    addTab(SteerConfig.GLOBAL);
    addTab(RimoConfig.GLOBAL);
    addTab(SafetyConfig.GLOBAL);
    addTab(LocalizationConfig.GLOBAL);
    addTab(JoystickConfig.GLOBAL);
    addTab(PursuitConfig.GLOBAL);
    addTab(ClusterConfig.GLOBAL);
    addTab(TrajectoryConfig.GLOBAL);
    addTab(MappingConfig.GLOBAL);
    addTab(SlamCoreConfig.GLOBAL);
    addTab(SlamPrcConfig.GLOBAL);
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
    ParametersComponent propertiesComponent = new ParametersComponent(object);
    jTabbedPane.addTab(object.getClass().getSimpleName(), propertiesComponent.getScrollPane());
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
