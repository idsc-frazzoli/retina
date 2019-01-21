// code by jph and mh
package ch.ethz.idsc.gokart.gui.lab;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.dev.linmot.LinmotConfig;
import ch.ethz.idsc.gokart.dev.linmot.LinmotSocket;
import ch.ethz.idsc.gokart.dev.rimo.RimoSocket;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Subdivide;

/** linmot press test enables the driver to apply the brake
 * at a constant value for a certain period of time */
public class LinmotSuccessivPressTestModule extends AbstractModule {
  private final JFrame jFrame = new JFrame();
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());
  private final LinmotPressTestLinmot linmotPressTestLinmot = new LinmotPressTestLinmot();
  private final LinmotPressTestRimo linmotPressTestRimo = new LinmotPressTestRimo();
  private int nextTest = 0;
  private JButton prev;
  private JButton next;
  private JButton test;
  private Tensor intensities;

  @Override
  protected void first() throws Exception {
    LinmotSocket.INSTANCE.addPutProvider(linmotPressTestLinmot);
    RimoSocket.INSTANCE.addPutProvider(linmotPressTestRimo);
    {
      final int n = LinmotConfig.GLOBAL.pressTestSteps.number().intValue();
      intensities = Subdivide.of(0, 1, n - 1);
      JPanel jPanel = new JPanel(new GridLayout(1, 3));
      List<JButton> list = new ArrayList<>();
      // button for previous test
      prev = new JButton("previous");
      list.add(prev);
      prev.addActionListener(actionEvent -> previous());
      jPanel.add(prev);
      // button for test
      test = new JButton("test: 0");
      list.add(test);
      test.addActionListener(actionEvent -> {
        list.forEach(button -> button.setEnabled(false));
        new Thread(new Runnable() {
          @Override
          public void run() {
            test();
            list.forEach(button -> button.setEnabled(true));
          }
        }).start();
      });
      jPanel.add(test);
      // button for next test
      next = new JButton("next");
      list.add(next);
      next.addActionListener(actionEvent -> next());
      jPanel.add(next);
      updateButtons();
      jFrame.setContentPane(jPanel);
    }
    windowConfiguration.attach(getClass(), jFrame);
    jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    jFrame.setVisible(true);
  }

  void next() {
    nextTest++;
    if (nextTest >= intensities.length())
      nextTest = 0;
    updateButtons();
  }

  void previous() {
    nextTest--;
    if (nextTest < 0)
      nextTest = intensities.length() - 1;
    updateButtons();
  }

  void reset() {
    nextTest = 0;
    updateButtons();
  }

  void test() {
    Scalar at = intensities.Get(nextTest);
    pressAt(at);
    next();
  }

  void updateButtons() {
    test.setText("Test [" + (nextTest + 1) + "/" + intensities.length() + "] at " + intensities.Get(nextTest));
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
    LinmotSuccessivPressTestModule linmotPressModule = new LinmotSuccessivPressTestModule();
    linmotPressModule.first();
    linmotPressModule.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  public static void main(String[] args) throws Exception {
    standalone();
  }
}
