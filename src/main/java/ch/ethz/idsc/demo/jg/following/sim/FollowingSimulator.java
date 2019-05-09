// code by gjoel
package ch.ethz.idsc.demo.jg.following.sim;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import javax.swing.JButton;
import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.gui.top.GeneralImageRender;
import ch.ethz.idsc.gokart.gui.top.TrajectoryDesignModule;
import ch.ethz.idsc.gokart.offline.video.BackgroundImage;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.retina.util.sys.AppCustomization;
import ch.ethz.idsc.retina.util.time.SystemTimestamp;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.Get;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.N;
import ch.ethz.idsc.tensor.sca.Round;

public class FollowingSimulator extends TrajectoryDesignModule {
  private static final ColorDataIndexed COLORS = ColorDataLists._001.cyclic();
  // ---
  private final SpinnerLabel<Scalar> spinnerLabelRate = new SpinnerLabel<>();
  private final SpinnerLabel<Scalar> spinnerLabelDuration = new SpinnerLabel<>();
  private final SpinnerLabel<Scalar> spinnerLabelSpeed = new SpinnerLabel<>();
  // ---
  private final Map<String, FollowingSimulations> map = new HashMap<>();
  private final RenderInterface renderInterface = new RenderInterface() {
    @Override
    public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
      int i = 0;
      for (Map.Entry<String, FollowingSimulations> entry : map.entrySet()) {
        Optional<Tensor> trail = entry.getValue().trail();
        if (trail.isPresent()) {
          graphics.setColor(COLORS.getColor(i));
          graphics.drawString(entry.getKey(), 0, (++i + 1) * graphics.getFont().getSize());
          graphics.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 9 }, 0) {
          });
          graphics.draw(geometricLayer.toPath2D(trail.get()));
        }
      }
    }
  };

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
      JButton jButton = new JButton("export");
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
      JButton jButton = new JButton("import");
      jButton.setToolTipText("import control points");
      jButton.addActionListener(actionEvent -> importTensor().map(tensor -> Tensor.of(tensor.stream().map(PoseHelper::toUnitless))) //
          .ifPresent(trajectoryDesign::setControlPointsSe2));
      trajectoryDesign.timerFrame.jToolBar.add(jButton);
    }
    {
      trajectoryDesign.timerFrame.jToolBar.addSeparator();
      File folder = new File("src/main/resources/dubilab/waypoints");
      folder.mkdirs();
      File file = new File(folder, DATE_FORMAT.format(new Date()) + ".csv");
      JButton jButton = new JButton("save waypoints");
      jButton.setToolTipText("save to " + file);
      jButton.addActionListener(actionEvent -> {
        try {
          Export.of(file, Tensor.of(trajectoryDesign.getControlPointsPose().stream().map(PoseHelper::toUnitless)).map(Round._4));
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      });
      trajectoryDesign.timerFrame.jToolBar.add(jButton);
      //
    }
    {
      trajectoryDesign.timerFrame.jToolBar.addSeparator();
      {
        Integer[] rates = { 1, 5, 10, 20, 30, 40, 50 };
        spinnerLabelRate.setStream(Arrays.stream(rates).map(i -> Quantity.of(i, SI.PER_SECOND)));
        spinnerLabelRate.setValue(Quantity.of(10, SI.PER_SECOND));
        spinnerLabelRate.addToComponentReduced(trajectoryDesign.timerFrame.jToolBar, new Dimension(50, 28), "rate");
      }
      {
        spinnerLabelDuration.setStream(IntStream.range(1, 11).map(i -> i * 10).mapToObj(i -> Quantity.of(i, SI.SECOND)));
        spinnerLabelDuration.setValue(Quantity.of(60, SI.SECOND));
        spinnerLabelDuration.addToComponentReduced(trajectoryDesign.timerFrame.jToolBar, new Dimension(50, 28), "duration");
      }
      {
        spinnerLabelSpeed.setStream(IntStream.range(1, 11).mapToObj(i -> Quantity.of(i, SI.VELOCITY)));
        spinnerLabelSpeed.setValue(Quantity.of(5, SI.VELOCITY));
        spinnerLabelSpeed.addToComponentReduced(trajectoryDesign.timerFrame.jToolBar, new Dimension(50, 28), "speed");
      }
    }
    {
      trajectoryDesign.timerFrame.jToolBar.addSeparator();
      JButton jButton = new JButton("simulate");
      jButton.setToolTipText("run pursuit simulations on current curve");
      jButton.addActionListener(actionEvent -> {
        Tensor curve = trajectoryDesign.getRefinedCurve().unmodifiable();
        if (Tensors.nonEmpty(curve)) {
          export(curve, "reference");
          Tensor initialPose = curve.get(0); // TODO GJOEL randomize
          for (FollowingSimulations simulation : FollowingSimulations.values()) {
            simulation.run(curve, initialPose, //
                spinnerLabelSpeed.getValue(), //
                spinnerLabelDuration.getValue(), //
                spinnerLabelRate.getValue().reciprocal());
            map.put(simulation.name(), simulation);
            System.out.println(simulation.getReport());
            export(simulation.trail().get(), simulation.name().toLowerCase());
          }
        } else
          System.out.println("no curve found!");
      });
      trajectoryDesign.timerFrame.jToolBar.add(jButton);
    }
    try {
      BackgroundImage backgroundImage = get20190408();
      GeneralImageRender generalImageRender = new GeneralImageRender(backgroundImage.bufferedImage, Inverse.of(backgroundImage.model2pixel));
      trajectoryDesign.timerFrame.geometricComponent.addRenderInterfaceBackground(generalImageRender);
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    trajectoryDesign.timerFrame.geometricComponent.addRenderInterface(renderInterface);
    trajectoryDesign.timerFrame.jFrame.setVisible(true);
  }

  /** @param tensor to be exported
   * @param name -> name_trail.csv */
  private static void export(Tensor tensor, String name) {
    try {
      File file = HomeDirectory.file(name + "_trail.csv");
      Export.of(file, Tensor.of(tensor.stream().map(PoseHelper::toUnitless)));
      System.out.println("exported " + file.getAbsolutePath());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) {
    FollowingSimulator simulator = new FollowingSimulator();
    simulator.first();
    simulator.trajectoryDesign.timerFrame.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }
}
