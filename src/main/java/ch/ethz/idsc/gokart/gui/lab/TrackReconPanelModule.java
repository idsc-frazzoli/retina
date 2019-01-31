// code by jph and mh
package ch.ethz.idsc.gokart.gui.lab;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.core.map.GokartTrackReconModule;
import ch.ethz.idsc.gokart.core.map.TrackReconMode;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.retina.util.sys.WindowConfiguration;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;

public class TrackReconPanelModule extends AbstractModule {
  private final JFrame jFrame = new JFrame();
  private final WindowConfiguration windowConfiguration = //
      AppCustomization.load(getClass(), new WindowConfiguration());
  private final GokartTrackReconModule gokartTrackReconModule = //
      ModuleAuto.INSTANCE.getInstance(GokartTrackReconModule.class);

  @Override // from AbstractModule
  protected void first() throws Exception {
    final boolean isAvailable = Objects.nonNull(gokartTrackReconModule);
    JPanel jPanel = new JPanel(new GridLayout(1, 3));
    {
      JButton jButton = new JButton("flag start & reset");
      jButton.addActionListener(actionEvent -> gokartTrackReconModule.flagStart());
      jButton.setEnabled(isAvailable);
      jPanel.add(jButton);
    }
    {
      JButton jButton = new JButton("reset track");
      jButton.addActionListener(actionEvent -> gokartTrackReconModule.resetTrack());
      jButton.setEnabled(isAvailable);
      jPanel.add(jButton);
    }
    {
      SpinnerLabel<TrackReconMode> spinnerLabel = new SpinnerLabel<>();
      spinnerLabel.setArray(TrackReconMode.values());
      spinnerLabel.setIndex(2);
      if (isAvailable)
        spinnerLabel.addSpinnerListener(gokartTrackReconModule::setMode);
      spinnerLabel.addToComponentReduced(jPanel, new Dimension(100, 100), "");
    }
    jFrame.setContentPane(jPanel);
    jFrame.setTitle(isAvailable //
        ? getClass().getSimpleName()
        : GokartTrackReconModule.class.getSimpleName() + " not running");
    windowConfiguration.attach(getClass(), jFrame);
    jFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    jFrame.setVisible(true);
  }

  @Override // from AbstractModule
  protected void last() {
    jFrame.setVisible(false);
    jFrame.dispose();
  }

  public static void standalone() throws Exception {
    TrackReconPanelModule linmotPressModule = new TrackReconPanelModule();
    linmotPressModule.first();
    linmotPressModule.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  public static void main(String[] args) throws Exception {
    standalone();
  }
}
