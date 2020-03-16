// code by gjoel
package ch.ethz.idsc.gokart.core.man;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.WindowConstants;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;

import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;

public class ManualOverrideModule extends AbstractModule {
  private final JFrame jFrame = new JFrame();
  private final WindowConfiguration windowConfiguration = AppCustomization.load(getClass(), new WindowConfiguration());
  private final Map<String, Class<? extends AbstractModule>> overrideMap = new HashMap<>();

  public ManualOverrideModule() {
    overrideMap.put("pedals", PredictiveTorqueVectoringModule.class);
    overrideMap.put("steering", SteerManualOverrideModule.class);
  }

  @Override // from AbstractModule
  protected void first() {
    {
      JPanel jPanel = new JPanel(new GridLayout(overrideMap.size(), 1));
      overrideMap.forEach((name, clazz) -> jPanel.add(jToggleButton(name, clazz)));
      jFrame.setContentPane(jPanel);
    }
    windowConfiguration.attach(getClass(), jFrame);
    jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    jFrame.setVisible(true);
  }

  @Override // from AbstractModule
  protected void last() {
    jFrame.setVisible(false);
    jFrame.dispose();
  }

  private static JToggleButton jToggleButton(String name, Class<? extends AbstractModule> clazz) {
    JToggleButton jToggleButton = new JToggleButton(name);
    jToggleButton.addActionListener(actionEvent -> {
      if (jToggleButton.isSelected()) {
        try {
          ModuleAuto.INSTANCE.runOne(clazz);
        } catch (Exception e) {
          e.printStackTrace();
        }
      } else
        ModuleAuto.INSTANCE.endOne(clazz);
    });
    return jToggleButton;
  }
}
