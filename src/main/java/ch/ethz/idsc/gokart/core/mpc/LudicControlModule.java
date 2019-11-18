// code by ta
package ch.ethz.idsc.gokart.core.mpc;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;
import ch.ethz.idsc.tensor.RealScalar;

public class LudicControlModule extends AbstractModule {
  private final JFrame jFrame = new JFrame();
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());

  @Override
  protected void first() {
    {
      JPanel jPanel = new JPanel(new GridLayout(4, 1));
      List<JButton> list = new ArrayList<>();
      {
        JButton jButton = new JButton("Beginner");
        list.add(jButton);
        jButton.addActionListener(actionEvent -> {
          System.out.println("Swapped to Beginner driving");
          MPCLudicConfig mpcLudicConfig = new MPCLudicConfig();
          mpcLudicConfig.speedCost = RealScalar.of(0.02);
          mpcLudicConfig.lagError = RealScalar.of(1);
          mpcLudicConfig.latError = RealScalar.of(0.3);
          mpcLudicConfig.progress = RealScalar.of(0.4);
          mpcLudicConfig.regularizerAB = RealScalar.of(0.0004);
          mpcLudicConfig.regularizerTV = RealScalar.of(0.1);
          mpcLudicConfig.slackSoftConstraint = RealScalar.of(10);
          mpcLudicConfig.pacejkaRD = MPCLudicConfig.GLOBAL.pacejkaRD;
          mpcLudicConfig.pacejkaFD = MPCLudicConfig.GLOBAL.pacejkaFD;
          MPCLudicConfig.FERRY = mpcLudicConfig;
        });
        jPanel.add(jButton);
      }
      {
        JButton jButton = new JButton("Moderate");
        list.add(jButton);
        jButton.addActionListener(actionEvent -> {
          System.out.println("Swapped to Moderate driving");
          MPCLudicConfig mpcLudicConfig = new MPCLudicConfig();
          mpcLudicConfig.speedCost = RealScalar.of(0.03);
          mpcLudicConfig.lagError = RealScalar.of(1);
          mpcLudicConfig.latError = RealScalar.of(0.06);
          mpcLudicConfig.progress = RealScalar.of(0.15);
          mpcLudicConfig.regularizerAB = RealScalar.of(0.0008);
          mpcLudicConfig.regularizerTV = RealScalar.of(0.01);
          mpcLudicConfig.slackSoftConstraint = RealScalar.of(8);
          mpcLudicConfig.pacejkaRD = MPCLudicConfig.GLOBAL.pacejkaRD;
          mpcLudicConfig.pacejkaFD = MPCLudicConfig.GLOBAL.pacejkaFD;
          MPCLudicConfig.FERRY = mpcLudicConfig;
        });
        jPanel.add(jButton);
      }
      {
        JButton jButton = new JButton("Advanced");
        list.add(jButton);
        jButton.addActionListener(actionEvent -> {
          System.out.println("Swapped to Advanced driving");
          MPCLudicConfig mpcLudicConfig = new MPCLudicConfig();
          mpcLudicConfig.speedCost = RealScalar.of(0.04);
          mpcLudicConfig.lagError = RealScalar.of(1);
          mpcLudicConfig.latError = RealScalar.of(0.01);
          mpcLudicConfig.progress = RealScalar.of(0.2);
          mpcLudicConfig.regularizerAB = RealScalar.of(0.0004);
          mpcLudicConfig.regularizerTV = RealScalar.of(0.01);
          mpcLudicConfig.slackSoftConstraint = RealScalar.of(5);
          mpcLudicConfig.pacejkaRD = MPCLudicConfig.GLOBAL.pacejkaRD;
          mpcLudicConfig.pacejkaFD = MPCLudicConfig.GLOBAL.pacejkaFD;
          MPCLudicConfig.FERRY = mpcLudicConfig;
        });
        jPanel.add(jButton);
      }
      {
        JButton jButton = new JButton("Drifting");
        list.add(jButton);
        jButton.addActionListener(actionEvent -> {
          System.out.println("Swapped to Drifting mode");
          MPCLudicConfig mpcLudicConfig = new MPCLudicConfig();
          mpcLudicConfig.speedCost = RealScalar.of(0.04);
          mpcLudicConfig.lagError = RealScalar.of(0.2);
          mpcLudicConfig.latError = RealScalar.of(0.01);
          mpcLudicConfig.progress = RealScalar.of(0.1);
          mpcLudicConfig.regularizerAB = RealScalar.of(0.0004);
          mpcLudicConfig.regularizerTV = RealScalar.of(0.05);
          mpcLudicConfig.slackSoftConstraint = RealScalar.of(4);
          mpcLudicConfig.pacejkaRD = MPCLudicConfig.GLOBAL.pacejkaRD;
          mpcLudicConfig.pacejkaFD = MPCLudicConfig.GLOBAL.pacejkaFD;
          MPCLudicConfig.FERRY = mpcLudicConfig;
        });
        jPanel.add(jButton);
      }
      {
        JButton jButton = new JButton("Custom");
        list.add(jButton);
        jButton.addActionListener(actionEvent -> {
          System.out.println("Swapped to Custom mode");
          MPCLudicConfig.FERRY = MPCLudicConfig.GLOBAL;
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
  }

  public static void standalone() throws Exception {
    LudicControlModule linmotPressModule = new LudicControlModule();
    linmotPressModule.first();
    linmotPressModule.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  public static void main(String[] args) throws Exception {
    standalone();
  }
}
