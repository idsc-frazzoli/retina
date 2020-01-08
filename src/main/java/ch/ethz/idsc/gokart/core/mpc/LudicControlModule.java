// code by ta
package ch.ethz.idsc.gokart.core.mpc;

import java.awt.GridLayout;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.qty.Quantity;

public class LudicControlModule extends AbstractModule {
  private final JFrame jFrame = new JFrame();
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());
  private Class<? extends MPCDrivingCommonModule> clazz = MPCDrivingLudicModule.class;

  @Override
  protected void first() {
    {
      JPanel jPanel = new JPanel(new GridLayout(5, 2));
      {
        jPanel.add(new JLabel("Power Steering:"));
      }
      {
        JToggleButton jToggleButton = new JToggleButton("Off");
        jToggleButton.addActionListener(actionEvent -> {
          endLudic();
          MPCLudicConfig.GLOBAL.powerSteer = jToggleButton.isSelected();
          jToggleButton.setText(jToggleButton.isSelected() ? "On" : "Off");
        });
        jPanel.add(jToggleButton);
      }
      {
        jPanel.add(new JLabel("Steering Mode:"));
      }
      {
        String[] choices = { "Angle", "Torque", "Combined" };
        JComboBox<String> jComboBox = new JComboBox<String>(choices);
        jComboBox.addActionListener(actionEvent -> {
          endLudic();
          switch (jComboBox.getSelectedIndex()) {
          case 0:// Use Ludic MPC model to command steering angle
            clazz = MPCDrivingLudicModule.class;
            break;
          case 1:// Use Torque MPC model to command torque
            clazz = MPCDrivingTorqueModule.class;
            break;
          case 2:// Use Torque MPC model, but command steering angle
            clazz = MPCDrivingCombinedTorqueModule.class;
            break;
          default:
            clazz = MPCDrivingLudicModule.class;
          }
          System.out.println(clazz);
        });
        jPanel.add(jComboBox);
      }
      {
        jPanel.add(new JLabel("Driver Mode:"));
        jPanel.add(new JSeparator());
      }
      {
        JButton jButton = new JButton("Beginner");
        jButton.addActionListener(actionEvent -> {
          System.out.println("Swapped to Beginner driving");
          MPCLudicConfig.FERRY = MPCLudicDriverConfigs.BEGINNER.get();
          startLudic();
        });
        jPanel.add(jButton);
      }
      {
        JButton jButton = new JButton("Moderate");
        jButton.addActionListener(actionEvent -> {
          System.out.println("Swapped to Moderate driving");
          MPCLudicConfig.FERRY = MPCLudicDriverConfigs.MODERATE.get();
          startLudic();
        });
        jPanel.add(jButton);
      }
      {
        JButton jButton = new JButton("Advanced");
        jButton.addActionListener(actionEvent -> {
          System.out.println("Swapped to Advanced driving");
          MPCLudicConfig.FERRY = MPCLudicDriverConfigs.ADVANCED.get();
          startLudic();
        });
        jPanel.add(jButton);
      }
      {
        JButton jButton = new JButton("Custom");
        jButton.addActionListener(actionEvent -> {
          System.out.println("Swapped to Custom mode");
          MPCLudicConfig.FERRY = MPCLudicConfig.GLOBAL;
          startLudic();
        });
        jPanel.add(jButton);
      }
      // also add turn of button
      jFrame.setContentPane(jPanel);
    }
    windowConfiguration.attach(getClass(), jFrame);
    jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    jFrame.setVisible(true);
  }

  @Override
  protected void last() {
    // ---
    jFrame.setVisible(false);
    jFrame.dispose();
    // ---
    endLudic();
  }

  public static void standalone() throws Exception {
    LudicControlModule linmotPressModule = new LudicControlModule();
    linmotPressModule.first();
    linmotPressModule.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  public static void main(String[] args) throws Exception {
    standalone();
  }

  private void startLudic() {
    if (Objects.isNull(ModuleAuto.INSTANCE.getInstance(clazz)))
      try {
        ModuleAuto.INSTANCE.runOne(clazz);
      } catch (Exception e) {
        e.printStackTrace();
      }
  }

  private void endLudic() {
    if (Objects.nonNull(ModuleAuto.INSTANCE.getInstance(clazz)))
      try {
        ModuleAuto.INSTANCE.endOne(clazz);
      } catch (Exception e) {
        e.printStackTrace();
      }
  }
}
