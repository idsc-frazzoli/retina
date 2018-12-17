// code by jph and mh
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

import ch.ethz.idsc.retina.util.gui.WindowConfiguration;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.AppCustomization;

public class TrackIdentificationButtons extends AbstractModule {
  private final JFrame jFrame = new JFrame();
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());
  private JButton recordTrack;
  private JButton setStart;
  public static boolean RECORDING = true;
  public static boolean SETTINGSTART = true;

  @Override
  protected void first() throws Exception {
    {
      // if this is used set it do default == false;
      RECORDING = false;
      JPanel jPanel = new JPanel(new GridLayout(1, 2));
      List<JButton> list = new ArrayList<>();
      // button for previous test
      recordTrack = new JButton("not sensing track");
      list.add(recordTrack);
      recordTrack.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          RECORDING = !RECORDING;
          if (RECORDING)
            recordTrack.setText("not sensing track");
          else
            recordTrack.setText("sensing track");
        }
      });
      jPanel.add(recordTrack);
      // button for test
      setStart = new JButton("set Start");
      list.add(setStart);
      setStart.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          SETTINGSTART = true;
        }
      });
      jPanel.add(setStart);
      jFrame.setContentPane(jPanel);
    }
    windowConfiguration.attach(getClass(), jFrame);
    jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    jFrame.setVisible(true);
  }

  @Override
  protected void last() {
    jFrame.setVisible(false);
    jFrame.dispose();
  }

  public static void standalone() throws Exception {
    TrackIdentificationButtons linmotPressModule = new TrackIdentificationButtons();
    linmotPressModule.first();
    linmotPressModule.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  public static void main(String[] args) throws Exception {
    standalone();
  }
}
