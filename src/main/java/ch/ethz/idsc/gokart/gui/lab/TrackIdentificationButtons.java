// code by jph and mh
package ch.ethz.idsc.gokart.gui.lab;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.core.map.GokartTrackIdentificationModule;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;

public class TrackIdentificationButtons extends AbstractModule {
  private final JFrame jFrame = new JFrame();
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());
  private final JToggleButton recordTrack = new JToggleButton("sense track");
  private final JButton setStart = new JButton("set start");
  private final JButton resetTrack = new JButton("reset track");
  private final GokartTrackIdentificationModule gokartTrackIdentificationModule = //
      ModuleAuto.INSTANCE.getInstance(GokartTrackIdentificationModule.class);

  @Override
  protected void first() throws Exception {
    {
      // if this is used set it do default == false;
      JPanel jPanel = new JPanel(new GridLayout(1, 2));
      // button for previous test
      boolean isAvailable = Objects.nonNull(gokartTrackIdentificationModule);
      recordTrack.setEnabled(isAvailable);
      if (isAvailable) {
        recordTrack.addActionListener(actionEvent -> gokartTrackIdentificationModule.setRecording(recordTrack.isSelected()));
        recordTrack.setSelected(gokartTrackIdentificationModule.isRecording());
      }
      jPanel.add(recordTrack);
      resetTrack.addActionListener(new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
          GokartTrackIdentificationModule gokartTrackIdentificationModule = //
              ModuleAuto.INSTANCE.getInstance(GokartTrackIdentificationModule.class);
          if (Objects.nonNull(gokartTrackIdentificationModule))
            gokartTrackIdentificationModule.resetTrack();
        }
      });
      jPanel.add(resetTrack);
      // button for test
      setStart.addActionListener(actionEvent -> gokartTrackIdentificationModule.findStart());
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
