// code by gjoel
package ch.ethz.idsc.demo.jg.following.sim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import javax.swing.JButton;
import javax.swing.WindowConstants;

import ch.ethz.idsc.gokart.gui.top.TrajectoryDesignModule;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.lie.se2.Se2Utils;
import ch.ethz.idsc.sophus.ply.Arrowhead;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.qty.Quantity;

/* package */ class FollowingSimulator extends TrajectoryDesignModule {
  private static final Scalar SIGMA_POS = Quantity.of(1, SI.METER);
  private static final Scalar DELTA_ANGLE = Pi.VALUE.divide(RealScalar.of(4));
  // ---
  private static final ColorDataIndexed COLORS = ColorDataLists._250.cyclic();
  // ---
  private final SpinnerLabel<Scalar> spinnerLabelRate = new SpinnerLabel<>();
  private final SpinnerLabel<Scalar> spinnerLabelDuration = new SpinnerLabel<>();
  private final SpinnerLabel<Scalar> spinnerLabelSpeed = new SpinnerLabel<>();
  // ---
  private Tensor initialPose = Tensors.empty();
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
          graphics.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 9 }, 0));
          graphics.draw(geometricLayer.toPath2D(trail.get()));
        }
        if (Tensors.nonEmpty(initialPose)) {
          geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(PoseHelper.toUnitless(initialPose)));
          Path2D path2d = geometricLayer.toPath2D(Arrowhead.of(.75));
          path2d.closePath();
          graphics.setColor(Color.MAGENTA);
          graphics.fill(path2d);
          geometricLayer.popMatrix();
        }
      }
    }
  };

  @Override // from AbstractModule
  protected void first() {
    super.first();
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
        spinnerLabelSpeed.setStream(IntStream.range(-5, 11).mapToObj(i -> Quantity.of(i, SI.VELOCITY)));
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
          initialPose = initialPose(curve);
          for (FollowingSimulations simulation : FollowingSimulations.values()) {
            simulation.run(curve, initialPose, //
                spinnerLabelSpeed.getValue(), //
                spinnerLabelDuration.getValue(), //
                spinnerLabelRate.getValue().reciprocal());
            map.put(simulation.name(), simulation);
            System.out.println(simulation.getReport().get());
            export(simulation.trail().get(), simulation.name().toLowerCase());
          }
        } else
          System.out.println("no curve found!");
      });
      trajectoryDesign.timerFrame.jToolBar.add(jButton);
    }
    trajectoryDesign.timerFrame.geometricComponent.addRenderInterface(renderInterface);
  }

  /** @param curve reference
   * @return randomized initial pose */
  private static Tensor initialPose(Tensor curve) {
    int idx = RandomVariate.of(UniformDistribution.of(0, curve.length())).number().intValue();
    Tensor initialPose = curve.get(idx);
    Tensor rnd = RandomVariate.of(NormalDistribution.of(Quantity.of(0, SI.METER), SIGMA_POS), 2);
    rnd.append(RandomVariate.of(UniformDistribution.of(DELTA_ANGLE.negate(), DELTA_ANGLE)));
    return initialPose.add(rnd);
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
