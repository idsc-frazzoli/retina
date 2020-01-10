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

import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;


public class LudicControlModule extends AbstractModule {
  private final JFrame jFrame = new JFrame();
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());
  private Class<? extends MPCDrivingCommonModule> clazz = MPCDrivingLudicModule.class;
  private boolean tParams=false;

  @Override
  protected void first() {
    {
      JPanel jPanel = new JPanel(new GridLayout(5, 2));
      {
        jPanel.add(new JLabel("LED Steering:"));
      }
      {
        JToggleButton jToggleButton = new JToggleButton("Off");
        jToggleButton.addActionListener(actionEvent -> {
          endLudic();
          MPCLudicConfig.GLOBAL.ledSteer = jToggleButton.isSelected();
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
          case 0: // use Ludic MPC model to command steering angle
            clazz = MPCDrivingLudicModule.class;
            tParams=false;
            break;
          case 1: // use Torque MPC model to command torque
            clazz = MPCDrivingTorqueModule.class;
            tParams=true;
            break;
          case 2: // use Torque MPC model, but command steering angle
            clazz = MPCDrivingCombinedTorqueModule.class;
            tParams=true;
            break;
          default:
            clazz = MPCDrivingLudicModule.class;
            tParams=false;
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
          if(tParams) {
            MPCLudicConfig.FERRY = MPCLudicDriverConfigs.BEGINNER_T.get();
          }else {
            MPCLudicConfig.FERRY = MPCLudicDriverConfigs.BEGINNER.get();
          }
          System.out.println("Max speed: "+MPCLudicConfig.FERRY.maxSpeed);
          startLudic();
        });
        jPanel.add(jButton);
      }
      {
        JButton jButton = new JButton("Moderate");
        jButton.addActionListener(actionEvent -> {
          System.out.println("Swapped to Moderate driving");
          if(tParams) {
            MPCLudicConfig.FERRY = MPCLudicDriverConfigs.MODERATE_T.get();
          }else {
            MPCLudicConfig.FERRY = MPCLudicDriverConfigs.MODERATE.get();
          }
          System.out.println("Max speed: "+ MPCLudicConfig.FERRY.maxSpeed);
          startLudic();
        });
        jPanel.add(jButton);
      }
      {
        JButton jButton = new JButton("Advanced");
        jButton.addActionListener(actionEvent -> {
          System.out.println("Swapped to Advanced driving");
          if(tParams) {
            MPCLudicConfig.FERRY = MPCLudicDriverConfigs.ADVANCED_T.get();
          }else {
            MPCLudicConfig.FERRY = MPCLudicDriverConfigs.ADVANCED.get();
          }
          System.out.println("Max speed: "+MPCLudicConfig.FERRY.maxSpeed);
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
          System.out.println("Max speed: "+MPCLudicConfig.FERRY.maxSpeed);
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
