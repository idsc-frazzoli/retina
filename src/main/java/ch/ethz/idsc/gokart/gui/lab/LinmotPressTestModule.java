// code by jph
package ch.ethz.idsc.gokart.gui.lab;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import ch.ethz.idsc.retina.dev.linmot.LinmotConfig;
import ch.ethz.idsc.retina.dev.linmot.LinmotSocket;
import ch.ethz.idsc.retina.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.sys.AbstractModule;
import ch.ethz.idsc.retina.sys.AppCustomization;
import ch.ethz.idsc.retina.util.gui.WindowConfiguration;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.sca.Round;

/**
 * 
 */
public class LinmotPressTestModule extends AbstractModule {
  private final JFrame jFrame = new JFrame();
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());
  private final LinmotPressTestLinmot linmotPressTestLinmot = new LinmotPressTestLinmot();
  private final LinmotPressTestRimo linmotPressTestRimo = new LinmotPressTestRimo();

  @Override
  protected void first() throws Exception {
    LinmotSocket.INSTANCE.addPutProvider(linmotPressTestLinmot);
    RimoSocket.INSTANCE.addPutProvider(linmotPressTestRimo);
    {
      final int n = LinmotConfig.GLOBAL.pressTestSteps.number().intValue();
      Tensor tensor = Subdivide.of(RationalScalar.of(1, 10), RealScalar.ONE, n - 1);
      JPanel jPanel = new JPanel(new GridLayout(n, 1));
      List<JButton> list = new ArrayList<>();
      for (int index = 0; index < n; ++index) {
        Scalar scalar = tensor.Get(index);
        JButton jButton = new JButton("" + scalar.map(Round._2));
        list.add(jButton);
        jButton.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            list.forEach(button -> button.setEnabled(false));
            new Thread(new Runnable() {
              @Override
              public void run() {
                pressAt(scalar);
                list.forEach(button -> button.setEnabled(true));
              }
            }).start();
          }
        });
        jPanel.add(jButton);
      }
      jFrame.setContentPane(jPanel);
    }
    windowConfiguration.attach(getClass(), jFrame);
    jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    jFrame.setVisible(true);
  }

  void pressAt(Scalar scalar) {
    linmotPressTestRimo.startPress();
    linmotPressTestLinmot.startPress(scalar);
    try {
      Thread.sleep(Magnitude.MILLI_SECOND.toLong(LinmotConfig.GLOBAL.pressTestDuration));
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    linmotPressTestRimo.stopPress();
    linmotPressTestLinmot.stopPress();
  }

  @Override
  protected void last() {
    RimoSocket.INSTANCE.removePutProvider(linmotPressTestRimo);
    LinmotSocket.INSTANCE.removePutProvider(linmotPressTestLinmot);
    // ---
    jFrame.setVisible(false);
    jFrame.dispose();
  }

  public static void standalone() throws Exception {
    LinmotPressTestModule linmotPressModule = new LinmotPressTestModule();
    linmotPressModule.first();
    linmotPressModule.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  public static void main(String[] args) throws Exception {
    standalone();
  }
}
