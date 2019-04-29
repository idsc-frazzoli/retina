// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Objects;

import javax.swing.JButton;
import javax.swing.WindowConstants;

import ch.ethz.idsc.demo.jg.FigureDubiGeodesicModule;
import ch.ethz.idsc.gokart.core.pure.FigureBaseModule;
import ch.ethz.idsc.gokart.offline.video.BackgroundImage;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.sys.ModuleAuto;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.io.Get;
import ch.ethz.idsc.tensor.io.MatrixForm;
import ch.ethz.idsc.tensor.io.Put;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.sca.Round;

public class TrajectoryDesignModule extends AbstractModule {
  public static final Tensor _20190401 = Tensors.of( //
      Tensors.vector(3.6677994336284594, 3.5436206505034793, -190.05265224432887), //
      Tensors.vector(3.5436206505034793, -3.6677994336284594, 74.03647376620074), //
      Tensors.vector(0.0, 0.0, 1.0));

  public static BackgroundImage get20190408() {
    return new BackgroundImage(ResourceData.bufferedImage("/dubilab/obstacles/20190408.png"), _20190401);
  }

  private final TrajectoryDesign trajectoryDesign = new TrajectoryDesign();

  @Override // from AbstractModule
  protected void first() {
    {
      final File file = AppCustomization.file(getClass(), "controlpoints.tensor");
      try {
        trajectoryDesign.setControl(Get.of(file));
      } catch (Exception exception) {
        // ---
      }
      trajectoryDesign.timerFrame.jFrame.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosed(WindowEvent windowEvent) {
          try {
            Put.of(file, trajectoryDesign.control());
          } catch (Exception exception) {
            exception.printStackTrace();
          }
        }
      });
    }
    trajectoryDesign.timerFrame.jToolBar.addSeparator();
    JButton jButton = new JButton("set curve");
    jButton.setToolTipText("override pursuit curve");
    jButton.addActionListener(actionEvent -> {
      Tensor curve = trajectoryDesign.getCurve().unmodifiable();
      System.out.println(Dimensions.of(curve));
      System.out.println("---");
      System.out.println(MatrixForm.of(trajectoryDesign.controlPoints().map(Round._4), ",", "", ""));
      // ---
      FigureDubiGeodesicModule geodesicModule = ModuleAuto.INSTANCE.getInstance(FigureDubiGeodesicModule.class);
      if (Objects.nonNull(geodesicModule))
        geodesicModule.setCurve(curve);
      ModuleAuto.INSTANCE.getExtensions(FigureBaseModule.class) //
          .forEach(figureBaseModule -> figureBaseModule.setCurve(curve));
    });
    trajectoryDesign.timerFrame.jToolBar.add(jButton);
    try {
      BackgroundImage backgroundImage = get20190408();
      GeneralImageRender generalImageRender = new GeneralImageRender(backgroundImage.bufferedImage, Inverse.of(backgroundImage.model2pixel));
      trajectoryDesign.timerFrame.geometricComponent.addRenderInterfaceBackground(generalImageRender);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    trajectoryDesign.timerFrame.geometricComponent.addRenderInterface(Dubilab.GRID_RENDER);
  }

  @Override // from AbstractModule
  protected void last() {
    trajectoryDesign.timerFrame.close();
  }

  public static void standalone() {
    TrajectoryDesignModule trajectoryDesignModule = new TrajectoryDesignModule();
    trajectoryDesignModule.first();
    trajectoryDesignModule.trajectoryDesign.timerFrame.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }

  public static void main(String[] args) throws Exception {
    standalone();
  }
}
