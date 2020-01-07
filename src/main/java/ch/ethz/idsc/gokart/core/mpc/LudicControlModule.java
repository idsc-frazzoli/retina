// code by ta
package ch.ethz.idsc.gokart.core.mpc;

import java.awt.GridLayout;
import java.util.Objects;
import javax.swing.JButton;
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
  private Class<? extends MPCDrivingCommonModule> clazz2 = MPCDrivingLudicModule.class;

  @Override
  protected void first() {
    {
      JPanel jPanel = new JPanel(new GridLayout(6, 2));
      {
        jPanel.add(new JLabel("Power Steering (requires restart):"));
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
        JToggleButton jToggleButton = new JToggleButton("Angle");
        jToggleButton.addActionListener(actionEvent -> {
          endLudic();
          if (jToggleButton.isSelected()) {
            clazz = MPCDrivingTorqueModule.class;
            clazz2 = MPCDrivingTorqueModule.class;
            jToggleButton.setText("Torque");
          } else {
            clazz = MPCDrivingLudicModule.class;
            clazz2 = MPCDrivingLudicModule.class;
            jToggleButton.setText("Angle");
          }
        });
        jPanel.add(jToggleButton);
      }
      {
        jPanel.add(new JLabel("Combined Mode:"));
      }
      {
        JToggleButton jToggleButton = new JToggleButton("Off");
        jToggleButton.addActionListener(actionEvent -> {
          endLudic();
          if (jToggleButton.isSelected()) {
            clazz = MPCDrivingCombinedTorqueModule.class;
            jToggleButton.setText("On");
          } else {
            clazz = clazz2;
            jToggleButton.setText("Off");
          }
        });
        jPanel.add(jToggleButton);
      }
      {
        jPanel.add(new JLabel("Driver Mode:"));
        jPanel.add(new JSeparator());
      }
      {
        JButton jButton = new JButton("Beginner");
        jButton.addActionListener(actionEvent -> {
          System.out.println("Swapped to Beginner driving");
          MPCLudicConfig mpcLudicConfig = new MPCLudicConfig();
          mpcLudicConfig.speedCost = RealScalar.of(0.02);
          mpcLudicConfig.lagError = RealScalar.of(1);
          mpcLudicConfig.latError = RealScalar.of(0.12);
          mpcLudicConfig.progress = RealScalar.of(0.1);
          mpcLudicConfig.regularizerAB = RealScalar.of(0.0012);
          mpcLudicConfig.regularizerTV = RealScalar.of(0.01);
          mpcLudicConfig.slackSoftConstraint = RealScalar.of(10);
          mpcLudicConfig.pacejkaRD = MPCLudicConfig.GLOBAL.pacejkaRD;
          mpcLudicConfig.pacejkaFD = MPCLudicConfig.GLOBAL.pacejkaFD;
          mpcLudicConfig.pacejkaRC = MPCLudicConfig.GLOBAL.pacejkaRC;
          mpcLudicConfig.pacejkaFC = MPCLudicConfig.GLOBAL.pacejkaFC;
          mpcLudicConfig.pacejkaRB = MPCLudicConfig.GLOBAL.pacejkaRB;
          mpcLudicConfig.pacejkaFB = MPCLudicConfig.GLOBAL.pacejkaFB;
          mpcLudicConfig.steerStiff = MPCLudicConfig.GLOBAL.steerStiff;
          mpcLudicConfig.steerDamp = MPCLudicConfig.GLOBAL.steerDamp;
          mpcLudicConfig.steerInertia = MPCLudicConfig.GLOBAL.steerInertia;
          mpcLudicConfig.maxSpeed = Quantity.of(5, SI.VELOCITY);
          MPCLudicConfig.FERRY = mpcLudicConfig;
          startLudic();
        });
        jPanel.add(jButton);
      }
      {
        JButton jButton = new JButton("Moderate");
        jButton.addActionListener(actionEvent -> {
          System.out.println("Swapped to Moderate driving");
          MPCLudicConfig mpcLudicConfig = new MPCLudicConfig();
          mpcLudicConfig.speedCost = RealScalar.of(0.02);
          mpcLudicConfig.lagError = RealScalar.of(1);
          mpcLudicConfig.latError = RealScalar.of(0.06);
          mpcLudicConfig.progress = RealScalar.of(0.15);
          mpcLudicConfig.regularizerAB = RealScalar.of(0.0008);
          mpcLudicConfig.regularizerTV = RealScalar.of(0.01);
          mpcLudicConfig.slackSoftConstraint = RealScalar.of(8);
          mpcLudicConfig.pacejkaRD = MPCLudicConfig.GLOBAL.pacejkaRD;
          mpcLudicConfig.pacejkaFD = MPCLudicConfig.GLOBAL.pacejkaFD;
          mpcLudicConfig.pacejkaRC = MPCLudicConfig.GLOBAL.pacejkaRC;
          mpcLudicConfig.pacejkaFC = MPCLudicConfig.GLOBAL.pacejkaFC;
          mpcLudicConfig.pacejkaRB = MPCLudicConfig.GLOBAL.pacejkaRB;
          mpcLudicConfig.pacejkaFB = MPCLudicConfig.GLOBAL.pacejkaFB;
          mpcLudicConfig.steerStiff = MPCLudicConfig.GLOBAL.steerStiff;
          mpcLudicConfig.steerDamp = MPCLudicConfig.GLOBAL.steerDamp;
          mpcLudicConfig.steerInertia = MPCLudicConfig.GLOBAL.steerInertia;
          mpcLudicConfig.maxSpeed = Quantity.of(8, SI.VELOCITY);
          MPCLudicConfig.FERRY = mpcLudicConfig;
          startLudic();
        });
        jPanel.add(jButton);
      }
      {
        JButton jButton = new JButton("Advanced");
        jButton.addActionListener(actionEvent -> {
          System.out.println("Swapped to Advanced driving");
          MPCLudicConfig mpcLudicConfig = new MPCLudicConfig();
          mpcLudicConfig.speedCost = RealScalar.of(0.03);
          mpcLudicConfig.lagError = RealScalar.of(1);
          mpcLudicConfig.latError = RealScalar.of(0.01);
          mpcLudicConfig.progress = RealScalar.of(0.3);
          mpcLudicConfig.regularizerAB = RealScalar.of(0.0004);
          mpcLudicConfig.regularizerTV = RealScalar.of(0.01);
          mpcLudicConfig.slackSoftConstraint = RealScalar.of(5);
          mpcLudicConfig.pacejkaRD = MPCLudicConfig.GLOBAL.pacejkaRD;
          mpcLudicConfig.pacejkaFD = MPCLudicConfig.GLOBAL.pacejkaFD;
          mpcLudicConfig.pacejkaRC = MPCLudicConfig.GLOBAL.pacejkaRC;
          mpcLudicConfig.pacejkaFC = MPCLudicConfig.GLOBAL.pacejkaFC;
          mpcLudicConfig.pacejkaRB = MPCLudicConfig.GLOBAL.pacejkaRB;
          mpcLudicConfig.pacejkaFB = MPCLudicConfig.GLOBAL.pacejkaFB;
          mpcLudicConfig.steerStiff = MPCLudicConfig.GLOBAL.steerStiff;
          mpcLudicConfig.steerDamp = MPCLudicConfig.GLOBAL.steerDamp;
          mpcLudicConfig.steerInertia = MPCLudicConfig.GLOBAL.steerInertia;
          mpcLudicConfig.maxSpeed = Quantity.of(12, SI.VELOCITY);
          MPCLudicConfig.FERRY = mpcLudicConfig;
          startLudic();
        });
        jPanel.add(jButton);
      }
      // {
      // JButton jButton = new JButton("Drifting");
      // list.add(jButton);
      // jButton.addActionListener(actionEvent -> {
      // System.out.println("Swapped to Drifting mode");
      // MPCLudicConfig mpcLudicConfig = new MPCLudicConfig();
      // mpcLudicConfig.speedCost = RealScalar.of(0.04);
      // mpcLudicConfig.lagError = RealScalar.of(0.2);
      // mpcLudicConfig.latError = RealScalar.of(0.01);
      // mpcLudicConfig.progress = RealScalar.of(0.1);
      // mpcLudicConfig.regularizerAB = RealScalar.of(0.0004);
      // mpcLudicConfig.regularizerTV = RealScalar.of(0.05);
      // mpcLudicConfig.slackSoftConstraint = RealScalar.of(4);
      // mpcLudicConfig.pacejkaRD = MPCLudicConfig.GLOBAL.pacejkaRD;
      // mpcLudicConfig.pacejkaFD = MPCLudicConfig.GLOBAL.pacejkaFD;
      // mpcLudicConfig.maxSpeed= Quantity.of(8, SI.VELOCITY);
      // MPCLudicConfig.FERRY = mpcLudicConfig;
      // });
      // jPanel.add(jButton);
      // }
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
