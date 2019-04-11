// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JToggleButton;

import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.core.slam.LidarLocalizationModule;
import ch.ethz.idsc.owl.gui.win.TimerFrame;
import ch.ethz.idsc.retina.util.sys.GuiConfig;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.mat.LinearSolve;

/* package */ class ViewLcmFrame extends TimerFrame {
  private static final Tensor MODEL2PIXEL_INITIAL = LocalizationConfig.getPredefinedMap().getModel2Pixel();
  // ---
  private final LidarLocalizationModule lidarLocalizationModule = //
      ModuleAuto.INSTANCE.getInstance(LidarLocalizationModule.class);
  final JButton jButtonMapCreate = GuiConfig.GLOBAL.createButton("map create");
  final JButton jButtonMapUpdate = GuiConfig.GLOBAL.createButton("map update");

  public ViewLcmFrame() {
    if (Objects.nonNull(lidarLocalizationModule)) {
      {
        JButton jButton = GuiConfig.GLOBAL.createButton("snap");
        jButton.addActionListener(actionEvent -> {
          setPose();
          lidarLocalizationModule.flagSnap();
          setPose();
        });
        jToolBar.add(jButton);
      }
      {
        JToggleButton jToggleButton = GuiConfig.GLOBAL.createToggleButton("track");
        jToggleButton.setSelected(lidarLocalizationModule.isTracking());
        jToggleButton.addActionListener(actionEvent -> lidarLocalizationModule.setTracking(jToggleButton.isSelected()));
        jToolBar.add(jToggleButton);
      }
      jToolBar.add(jButtonMapCreate);
      jToolBar.add(jButtonMapUpdate);
    }
    geometricComponent.setModel2Pixel(MODEL2PIXEL_INITIAL);
    Tensor tensor = geometricComponent.getModel2Pixel();
    System.out.println("m2p=" + Pretty.of(tensor));
  }

  private void setPose() {
    Tensor model2pixel = geometricComponent.getModel2Pixel(); // quantify drag by user
    Tensor init = lidarLocalizationModule.getPose(); // {x[m], y[m], angle}
    Tensor gokart = GokartPoseHelper.toSE2Matrix(init);
    Tensor newPose = LinearSolve.of(MODEL2PIXEL_INITIAL, model2pixel.dot(gokart));
    lidarLocalizationModule.resetPose(GokartPoseHelper.attachUnits(Se2Utils.fromSE2Matrix(newPose)));
    geometricComponent.setModel2Pixel(MODEL2PIXEL_INITIAL); // undo drag by user
  }
}
