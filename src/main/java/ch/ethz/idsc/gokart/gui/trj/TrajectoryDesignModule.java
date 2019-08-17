// code by jph
package ch.ethz.idsc.gokart.gui.trj;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.core.plan.TrajectoryConfig;
import ch.ethz.idsc.gokart.gui.GokartLcmChannel;
import ch.ethz.idsc.gokart.gui.top.Dubilab;
import ch.ethz.idsc.gokart.gui.top.GeneralImageRender;
import ch.ethz.idsc.gokart.lcm.mod.Se2CurveLcm;
import ch.ethz.idsc.gokart.offline.video.BackgroundImage;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.retina.util.sys.AbstractModule;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.time.SystemTimestamp;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.alg.Dimensions;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.Get;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.MatrixForm;
import ch.ethz.idsc.tensor.io.Put;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.sca.N;
import ch.ethz.idsc.tensor.sca.Round;

// TODO JPH auto update graphics when control points change
public class TrajectoryDesignModule extends AbstractModule {
  protected static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
  // ---
  protected final TrajectoryDesign trajectoryDesign = new TrajectoryDesign();

  @Override // from AbstractModule
  protected void first() {
    {
      final File file = AppCustomization.file(getClass(), "controlpoints.tensor");
      try {
        trajectoryDesign.setControlPointsSe2(Get.of(file));
      } catch (Exception exception) {
        // ---
      }
      trajectoryDesign.timerFrame.jFrame.addWindowListener(new WindowAdapter() {
        @Override
        public void windowClosed(WindowEvent windowEvent) {
          exportTensor(file, trajectoryDesign.getControlPointsSe2().map(N.DOUBLE::of));
        }
      });
    }
    {
      trajectoryDesign.timerFrame.jToolBar.addSeparator();
      JButton jButton = new JButton("set curve");
      jButton.setToolTipText("publish pursuit curve via lcm");
      jButton.addActionListener(actionEvent -> {
        Tensor curve = trajectoryDesign.getRefinedCurve().unmodifiable();
        Se2CurveLcm.publish(GokartLcmChannel.PURSUIT_CURVE_SE2, curve);
        System.out.println(Dimensions.of(curve));
        System.out.println("---");
        System.out.println(MatrixForm.of(trajectoryDesign.getControlPointsPose().map(Round._4), ",", "", ""));
      });
      trajectoryDesign.timerFrame.jToolBar.add(jButton);
    }
    {
      trajectoryDesign.timerFrame.jToolBar.addSeparator();
      JButton jButton = new JButton("save");
      jButton.setToolTipText("export control points");
      jButton.addActionListener(actionEvent -> {
        File file = HomeDirectory.file("Desktop", "controlpoints_" + SystemTimestamp.asString(new Date()) + ".tensor");
        exportTensor(file, trajectoryDesign.getControlPointsPose());
        System.out.println("exported control points to " + file.getAbsolutePath());
      });
      trajectoryDesign.timerFrame.jToolBar.add(jButton);
    }
    {
      trajectoryDesign.timerFrame.jToolBar.addSeparator();
      JButton jButton = new JButton("load");
      jButton.setToolTipText("import control points");
      jButton.addActionListener(actionEvent -> importTensor().map(tensor -> Tensor.of(tensor.stream().map(PoseHelper::toUnitless))) //
          .ifPresent(trajectoryDesign::setControlPointsSe2));
      trajectoryDesign.timerFrame.jToolBar.add(jButton);
    }
    {
      trajectoryDesign.timerFrame.jToolBar.addSeparator();
      File folder = new File("src/main/resources/dubilab");
      File subRaw = new File(folder, "rawpoints");
      subRaw.mkdirs();
      File subWay = new File(folder, "waypoints");
      subWay.mkdirs();
      String name = DATE_FORMAT.format(new Date()) + ".csv";
      File rawFile = new File(subRaw, name);
      File wayFile = new File(subWay, name);
      JButton jButton = new JButton("save waypoints");
      jButton.setToolTipText("save to " + wayFile);
      jButton.addActionListener(actionEvent -> {
        try {
          Export.of(rawFile, Tensor.of(trajectoryDesign.getControlPointsPose().stream().map(PoseHelper::toUnitless)).map(Round._4));
          Export.of(wayFile, Tensor.of(TrajectoryConfig.GLOBAL.resampledWaypoints(trajectoryDesign.getControlPointsPose(), true) //
              .stream().map(PoseHelper::toUnitless)).map(Round._4));
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      });
      trajectoryDesign.timerFrame.jToolBar.add(jButton);
      //
    }
    try {
      BackgroundImage backgroundImage = Dubilab.backgroundImage20190408();
      GeneralImageRender generalImageRender = new GeneralImageRender(backgroundImage.bufferedImage(), Inverse.of(backgroundImage.model2pixel()));
      trajectoryDesign.timerFrame.geometricComponent.addRenderInterfaceBackground(generalImageRender);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    trajectoryDesign.timerFrame.geometricComponent.addRenderInterface(Dubilab.GRID_RENDER);
    trajectoryDesign.timerFrame.jFrame.setVisible(true);
  }

  @Override // from AbstractModule
  protected void last() {
    trajectoryDesign.timerFrame.close();
  }

  protected static void exportTensor(File file, Tensor tensor) {
    try {
      Put.of(file, tensor);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }

  protected static Optional<Tensor> importTensor() {
    JFileChooser fileChooser = new JFileChooser();
    int returnVal = fileChooser.showOpenDialog(fileChooser);
    if (returnVal == JFileChooser.APPROVE_OPTION)
      try {
        File file = fileChooser.getSelectedFile();
        return Optional.of(Get.of(file));
      } catch (Exception exception) {
        exception.printStackTrace();
      }
    return Optional.empty();
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
