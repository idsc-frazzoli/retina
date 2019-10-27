// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JToggleButton;

import ch.ethz.idsc.gokart.core.slam.LidarLocalizationModule;
import ch.ethz.idsc.gokart.core.slam.LocalizationConfig;
import ch.ethz.idsc.owl.gui.win.TimerFrame;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.retina.util.sys.GuiConfig;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.UserName;
import ch.ethz.idsc.tensor.mat.LinearSolve;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ class ViewLcmFrame extends TimerFrame {
  private static final Tensor MODEL2PIXEL_INITIAL = LocalizationConfig.GLOBAL.getPredefinedMap().getModel2Pixel();
  // ---
  private final LidarLocalizationModule lidarLocalizationModule = //
      ModuleAuto.INSTANCE.getInstance(LidarLocalizationModule.class);

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
      if (UserName.is("datahaki")) {
        JButton jButton = GuiConfig.GLOBAL.createButton("get");
        jButton.addActionListener(e -> System.out.println("pose=" + lidarLocalizationModule.getPose().map(Round._5)));
        jToolBar.add(jButton);
      }
    }
    geometricComponent.setModel2Pixel(MODEL2PIXEL_INITIAL);
  }

  private void setPose() {
    Tensor model2pixel = geometricComponent.getModel2Pixel(); // quantify drag by user
    Tensor init = lidarLocalizationModule.getPose(); // {x[m], y[m], angle}
    Tensor gokart = PoseHelper.toSE2Matrix(init);
    Tensor newPose = LinearSolve.of(MODEL2PIXEL_INITIAL, model2pixel.dot(gokart));
    lidarLocalizationModule.resetPose(PoseHelper.attachUnits(Se2Matrix.toVector(newPose)));
    geometricComponent.setModel2Pixel(MODEL2PIXEL_INITIAL); // undo drag by user
  }
}
