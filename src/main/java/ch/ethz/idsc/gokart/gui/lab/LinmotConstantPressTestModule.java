// code by jph and mh
package ch.ethz.idsc.gokart.gui.lab;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.dev.linmot.LinmotSocket;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;

/** linmot press test enables the driver to apply the brake
 * at a constant value for a certain period of time
 * 
 * TODO MH generate a report from the log files about the brake effect */
public class LinmotConstantPressTestModule extends AbstractModule {
  private final JFrame jFrame = new JFrame();
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());
  private final LinmotConstantPressTestLinmot linmotConstantPressTestLinmot = new LinmotConstantPressTestLinmot();
  private JButton prev;
  private JButton next;
  private JButton test;
  private JButton setoff;
  private short position = -50;

  @Override
  protected void first() throws Exception {
    LinmotSocket.INSTANCE.addPutProvider(linmotConstantPressTestLinmot);
    {
      JPanel jPanel = new JPanel(new GridLayout(2, 2));
      List<JButton> list = new ArrayList<>();
      // button for previous test
      prev = new JButton("previous");
      list.add(prev);
      prev.addActionListener(actionEvent -> previous());
      jPanel.add(prev);
      // button for next test
      next = new JButton("next");
      list.add(next);
      next.addActionListener(actionEvent -> next());
      jPanel.add(next);
      // button for test
      test = new JButton("set Active");
      list.add(test);
      test.addActionListener(actionEvent -> switchActive());
      jPanel.add(test);
      // button for test
      setoff = new JButton("set off");
      list.add(setoff);
      setoff.addActionListener(actionEvent -> switchOff());
      jPanel.add(setoff);
      updateButtons();
      jFrame.setContentPane(jPanel);
    }
    windowConfiguration.attach(getClass(), jFrame);
    jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    jFrame.setVisible(true);
  }

  void next() {
    position -= 10;
    linmotConstantPressTestLinmot.setPosition(position);
    updateButtons();
  }

  void previous() {
    position += 10;
    linmotConstantPressTestLinmot.setPosition(position);
    updateButtons();
  }

  void switchOff() {
    linmotConstantPressTestLinmot.setOff(!linmotConstantPressTestLinmot.getIsOff());
    System.out.println(linmotConstantPressTestLinmot.getIsOff());
    updateButtons();
  }

  void switchActive() {
    linmotConstantPressTestLinmot.setPosition(position);
    linmotConstantPressTestLinmot.setActive(!linmotConstantPressTestLinmot.getIsActive());
    updateButtons();
  }

  void updateButtons() {
    String postext = " pos: " + position;
    if (linmotConstantPressTestLinmot.getIsActive())
      test.setText("deactivate" + postext);
    else
      test.setText("activate" + postext);
    if (linmotConstantPressTestLinmot.getIsOff())
      setoff.setText("turn current on");
    else
      setoff.setText("turn current off");
  }

  @Override
  protected void last() {
    LinmotSocket.INSTANCE.removePutProvider(linmotConstantPressTestLinmot);
    // ---
    jFrame.setVisible(false);
    jFrame.dispose();
  }

  public static void standalone() throws Exception {
    LinmotConstantPressTestModule linmotPressModule = new LinmotConstantPressTestModule();
    linmotPressModule.first();
    linmotPressModule.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  public static void main(String[] args) throws Exception {
    standalone();
  }
}
