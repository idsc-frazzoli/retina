// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.event.ActionListener;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.JToggleButton;

import ch.ethz.idsc.gokart.core.pos.GokartPoseHelper;
import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.core.pos.MappedPoseInterface;
import ch.ethz.idsc.gokart.core.slam.LidarLocalizationModule;
import ch.ethz.idsc.owl.gui.win.TimerFrame;
import ch.ethz.idsc.retina.util.sys.GuiConfig;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.sophus.group.Se2Utils;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.io.Pretty;
import ch.ethz.idsc.tensor.mat.LinearSolve;
import ch.ethz.idsc.tensor.sca.Round;

public class ViewLcmFrame extends TimerFrame {
  private final Tensor MODEL2PIXEL_INITIAL = LocalizationConfig.getPredefinedMap().getModel2Pixel();
  private final LidarLocalizationModule lidarLocalizationModule = //
      ModuleAuto.INSTANCE.getInstance(LidarLocalizationModule.class);
  private final JButton jButtonSetLocation1 = GuiConfig.GLOBAL.createButton("1 set");
  private final JButton jButtonSnap = GuiConfig.GLOBAL.createButton("2 snap");
  private final JButton jButtonSetLocation2 = GuiConfig.GLOBAL.createButton("3 set (again)");
  private final JToggleButton jToggleButton = GuiConfig.GLOBAL.createToggleButton("4 track");
  final JButton jButtonMapCreate = GuiConfig.GLOBAL.createButton("map create");
  final JButton jButtonMapUpdate = GuiConfig.GLOBAL.createButton("map update");
  private MappedPoseInterface mappedPoseInterface;
  private final ActionListener actionListener = actionEvent -> {
    Tensor model2pixel = geometricComponent.getModel2Pixel();
    Tensor state = mappedPoseInterface.getPose(); // {x[m], y[m], angle}
    Tensor pose = GokartPoseHelper.toSE2Matrix(state);
    Tensor newPose = LinearSolve.of(MODEL2PIXEL_INITIAL, model2pixel.dot(pose));
    Tensor newState = GokartPoseHelper.attachUnits(Se2Utils.fromSE2Matrix(newPose));
    System.out.println("pose=" + newState.map(Round._5));
    mappedPoseInterface.setPose(newState, RealScalar.ONE);
    geometricComponent.setModel2Pixel(MODEL2PIXEL_INITIAL);
  };

  public ViewLcmFrame() {
    if (Objects.nonNull(lidarLocalizationModule)) {
      jButtonSetLocation1.addActionListener(actionListener);
      jToolBar.add(jButtonSetLocation1);
      jButtonSnap.addActionListener(e -> lidarLocalizationModule.flagSnap());
      jToolBar.add(jButtonSnap);
      jButtonSetLocation2.addActionListener(actionListener);
      jToolBar.add(jButtonSetLocation2);
      jToggleButton.setSelected(lidarLocalizationModule.isTracking());
      jToggleButton.addActionListener(e -> lidarLocalizationModule.setTracking(jToggleButton.isSelected()));
      jToolBar.add(jToggleButton);
      jToolBar.add(jButtonMapCreate);
      jToolBar.add(jButtonMapUpdate);
    }
    geometricComponent.setModel2Pixel(MODEL2PIXEL_INITIAL);
    // Tensors.fromString("{{7.5,0,300},{0,-7.5,300},{0,0,1}}"));
    Tensor tensor = geometricComponent.getModel2Pixel();
    System.out.println("m2p=" + Pretty.of(tensor));
  }

  protected void setGokartPoseInterface(MappedPoseInterface mappedPoseInterface) {
    this.mappedPoseInterface = mappedPoseInterface;
  }
}
