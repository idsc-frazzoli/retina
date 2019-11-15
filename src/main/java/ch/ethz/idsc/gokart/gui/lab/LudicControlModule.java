// code by ta
package ch.ethz.idsc.gokart.gui.lab;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.core.mpc.MPCLudicConfig;
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
          synchronized (MPCLudicConfig.GLOBAL) {
          MPCLudicConfig.GLOBAL.speedCost = RealScalar.of(0.4);
          MPCLudicConfig.GLOBAL.lagError = RealScalar.of(1);
          MPCLudicConfig.GLOBAL.latError = RealScalar.of(0.3);
          MPCLudicConfig.GLOBAL.progress = RealScalar.of(0.4);
          MPCLudicConfig.GLOBAL.regularizerAB = RealScalar.of(0.0004);
          MPCLudicConfig.GLOBAL.regularizerTV = RealScalar.of(0.1);
          MPCLudicConfig.GLOBAL.slackSoftConstraint = RealScalar.of(10);
          }
        });
        jPanel.add(jButton);
      }
      {
        JButton jButton = new JButton("Moderate");
        list.add(jButton);
        jButton.addActionListener(actionEvent -> {
          System.out.println("Swapped to Moderate driving");
          synchronized (MPCLudicConfig.GLOBAL) {
          MPCLudicConfig.GLOBAL.speedCost = RealScalar.of(0.08);
          MPCLudicConfig.GLOBAL.lagError = RealScalar.of(1);
          MPCLudicConfig.GLOBAL.latError = RealScalar.of(0.06);
          MPCLudicConfig.GLOBAL.progress = RealScalar.of(0.15);
          MPCLudicConfig.GLOBAL.regularizerAB = RealScalar.of(0.0008);
          MPCLudicConfig.GLOBAL.regularizerTV = RealScalar.of(0.01);
          MPCLudicConfig.GLOBAL.slackSoftConstraint = RealScalar.of(8);
          }
        });
        jPanel.add(jButton);
      }
      {
        JButton jButton = new JButton("Advanced");
        list.add(jButton);
        jButton.addActionListener(actionEvent -> {
          System.out.println("Swapped to Advanced driving");
          synchronized (MPCLudicConfig.GLOBAL) {
          MPCLudicConfig.GLOBAL.speedCost = RealScalar.of(0.04);
          MPCLudicConfig.GLOBAL.lagError = RealScalar.of(1);
          MPCLudicConfig.GLOBAL.latError = RealScalar.of(0.01);
          MPCLudicConfig.GLOBAL.progress = RealScalar.of(0.2);
          MPCLudicConfig.GLOBAL.regularizerAB = RealScalar.of(0.0004);         
          MPCLudicConfig.GLOBAL.regularizerTV = RealScalar.of(0.01);
          MPCLudicConfig.GLOBAL.slackSoftConstraint = RealScalar.of(5);
          }
        });
        jPanel.add(jButton);
      }
      {
        JButton jButton = new JButton("Drifting");
        list.add(jButton);
        jButton.addActionListener(actionEvent -> {
          System.out.println("Swapped to Drifting mode");
          synchronized (MPCLudicConfig.GLOBAL) {
          MPCLudicConfig.GLOBAL.speedCost = RealScalar.of(0.04);
          MPCLudicConfig.GLOBAL.lagError = RealScalar.of(0.2);
          MPCLudicConfig.GLOBAL.latError = RealScalar.of(0.01);
          MPCLudicConfig.GLOBAL.progress = RealScalar.of(0.1);
          MPCLudicConfig.GLOBAL.regularizerAB = RealScalar.of(0.0004);         
          MPCLudicConfig.GLOBAL.regularizerTV = RealScalar.of(0.05);
          MPCLudicConfig.GLOBAL.slackSoftConstraint = RealScalar.of(4);
          }
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
