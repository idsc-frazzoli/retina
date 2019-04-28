// code by jph
package ch.ethz.idsc.gokart.gui.top;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.offline.video.BackgroundImage;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.io.MatrixForm;
import ch.ethz.idsc.tensor.io.ResourceData;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.sca.Round;

public class TrajectoryDesignModule extends AbstractModule {
  public static final Tensor _20190401 = Tensors.fromString( //
      "{{3.6677994336284594, 3.5436206505034793, -190.05265224432887}, {3.5436206505034793, -3.6677994336284594, 74.03647376620074}, {0.0, 0.0, 1.0}}");
  // ---
  // private static final File IMAGE_FILE = HomeDirectory.Pictures("20190408_gray_ts.png");

  public static BackgroundImage get20190408() {
    // return BackgroundImage.from(IMAGE_FILE, _20190401);
    return new BackgroundImage(ResourceData.bufferedImage("/dubilab/obstacles/20190408.png"), _20190401);
  }

  private final TrajectoryDesign trajectoryDesign = new TrajectoryDesign();

  @Override
  protected void first() {
    trajectoryDesign.timerFrame.jToolBar.addSeparator();
    JButton jButton = new JButton("set curve");
    jButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.out.println(Dimensions.of(trajectoryDesign.getCurve()));
        System.out.println("---");
        System.out.println(MatrixForm.of(trajectoryDesign.controlPoints().map(Round._4), ",", "", ""));
      }
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

  @Override
  protected void last() {
    // ---
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
