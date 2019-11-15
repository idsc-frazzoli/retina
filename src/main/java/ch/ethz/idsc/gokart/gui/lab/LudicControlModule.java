// code by jph
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
        JButton jButton = new JButton("simple");
        list.add(jButton);
        jButton.addActionListener(actionEvent -> {
          System.out.println("here butten was pressed");
          synchronized (MPCLudicConfig.GLOBAL) {
            MPCLudicConfig.GLOBAL.speedCost = RealScalar.of(0.03);
          }
        });
        jPanel.add(jButton);
      }
      {
        JButton jButton = new JButton("medium");
        list.add(jButton);
        jButton.addActionListener(actionEvent -> {
          System.out.println("here butten was pressed");
          MPCLudicConfig.GLOBAL.speedCost = RealScalar.of(0.09);
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
