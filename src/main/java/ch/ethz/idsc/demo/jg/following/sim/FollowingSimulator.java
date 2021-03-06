// code by gjoel
package ch.ethz.idsc.demo.jg.following.sim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import javax.swing.JButton;
import javax.swing.WindowConstants;

import ch.ethz.idsc.demo.jg.following.analysis.ErrorDistributions;
import ch.ethz.idsc.gokart.gui.trj.TrajectoryDesignModule;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.pose.PoseHelper;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.sophus.ply.Arrowhead;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Transpose;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.opt.Pi;
import ch.ethz.idsc.tensor.pdf.NormalDistribution;
import ch.ethz.idsc.tensor.pdf.RandomVariate;
import ch.ethz.idsc.tensor.pdf.UniformDistribution;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Mean;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ class FollowingSimulator extends TrajectoryDesignModule {
  private static final Scalar SIGMA_POS = Quantity.of(1, SI.METER);
  private static final Scalar DELTA_ANGLE = Pi.VALUE.divide(RealScalar.of(4));
  private static final String[] ERROR_TYPES = { "position error", "heading error" };
  private static final Scalar[] BIN_SIZES = { Quantity.of(0.01, "m"), RealScalar.of(0.01) };
  // ---
  private static final ColorDataIndexed COLORS = ColorDataLists._250.cyclic();
  // ---
  private final SpinnerLabel<Scalar> spinnerLabelRate = new SpinnerLabel<>();
  private final SpinnerLabel<Scalar> spinnerLabelDuration = new SpinnerLabel<>();
  private final SpinnerLabel<Scalar> spinnerLabelSpeed = new SpinnerLabel<>();
  // ---
  private final boolean latex = System.getProperty("user.name").equals("joelg");
  private int rep = 1;
  // ---
  private Tensor initialPose = Tensors.empty();
  private final Map<String, FollowingSimulations> map = new HashMap<>();
  private final Map<String, Tensor> averaging = new HashMap<>();
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
          geometricLayer.pushMatrix(Se2Matrix.of(PoseHelper.toUnitless(initialPose)));
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
                Tensors.of(spinnerLabelSpeed.getValue(), Quantity.of(0, SI.VELOCITY), Quantity.of(0, SI.PER_SECOND)), //
                spinnerLabelDuration.getValue(), //
                spinnerLabelRate.getValue().reciprocal());
            map.put(simulation.identifier(), simulation);
            if (simulation.averageError().get().Get(0).number().doubleValue() < 100)
              averaging.put(simulation.identifier(),
                  averaging.getOrDefault(simulation.identifier(), Tensors.empty()).append(Tensors.of(simulation.averageError().get().Get(0), //
                      simulation.maximumError().get().Get(0), //
                      simulation.averageError().get().Get(1), //
                      simulation.maximumError().get().Get(1) //
              )));
            System.out.println(simulation.getReport().get());
            export(simulation.trail().get(), simulation.name().toLowerCase());
          }
          if (latex)
            System.out.println(latex(FollowingSimulations.values(), "smooth"));
          try {
            plot();
          } catch (IOException e) {
            e.printStackTrace();
          }
        } else
          System.out.println("no curve found!");
      });
      trajectoryDesign.timerFrame.jToolBar.add(jButton);
    }
    {
      JButton jButton = new JButton("avg");
      jButton.setToolTipText("print and reset averages");
      jButton.addActionListener(actionEvent -> {
        averaging.forEach((identifier, tensor) -> {
          Tensor avg = Tensor.of(Transpose.of(tensor).stream().map(Mean::of));
          System.out.println(identifier + avg.map(Round._4));
        });
        if (latex) {
          String latex = "";
          int i = 0;
          for (FollowingSimulations simulation : FollowingSimulations.values()) {
            Tensor avg = Tensor.of(Transpose.of(averaging.get(simulation.identifier())).stream().map(Mean::of)).map(Round._4);
            String base;
            if (i == 0)
              base = "\\multirow{4}{*}{?} & pure pursuit & 3.5 & %.4f & %.4f & %.4f & %.4f \\\\ \\cline{2-7}\n";
            else if (i == 1)
              base = "& \\multirow{3}{*}{clothoid pursuit} & 3.5 & %.4f & %.4f & %.4f & %.4f \\\\\n";
            else
              base = "&& " + (i == 2 ? 5 : 7) + " & %.4f & %.4f & %.4f & %.4f \\\\\n";
            latex += String.format(Locale.US, base, //
                avg.Get(0).number().doubleValue(), avg.Get(1).number().doubleValue(), //
                avg.Get(2).number().doubleValue(), avg.Get(3).number().doubleValue());
            i++;
          }
          System.out.println(latex + "\\hline");
        }
        averaging.clear();
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

  private void plot() throws IOException {
    Tensor[] errors = map.values().stream().map(FollowingSimulations::errors).map(Transpose::of).toArray(Tensor[]::new);
    String[] identifiers = map.keySet().toArray(new String[map.size()]);
    ErrorDistributions.plot(errors, identifiers, ERROR_TYPES, BIN_SIZES);
  }

  /** @param simulations
   * @param track
   * @return partial latex table code
   * before:
   * \begin{table}[H]
   * \begin{tabular}{@{\extracolsep{4pt}}ccccccc}
   * \hline
   * \multirow{2}{*}{\textbf{track (run)}} & \multirow{2}{*}{\textbf{controller}} & \multirow{2}{*}{\textbf{look ahead [m]}}
   * & \multicolumn{2}{c}{\textbf{position [m]}} & \multicolumn{2}{c}{\textbf{heading}} \\ \cline{4-5} \cline{6-7}
   * &&& \textbf{avg} & \textbf{max} & \textbf{avg} & \textbf{max} \\
   * \hline \hline
   * after:
   * \end{tabular}
   * \end{table} */
  private String latex(FollowingSimulations[] simulations, String track) {
    String latex = "";
    int i = 0;
    for (FollowingSimulations simulation : simulations) {
      Tensor avg = simulation.averageError().get().map(Round._4);
      Tensor max = simulation.maximumError().get().map(Round._4);
      if (i == 0)
        latex += String.format(Locale.US, "\\multirow{4}{*}{%s (%d)} & pure pursuit & 3.5 & %.4f & %.4f & %.4f & %.4f \\\\ \\cline{2-7}\n", //
            track, rep++, avg.Get(0).number().doubleValue(), max.Get(0).number().doubleValue(), //
            avg.Get(1).number().doubleValue(), max.Get(1).number().doubleValue());
      else if (i == 1)
        latex += String.format(Locale.US, "& \\multirow{3}{*}{clothoid pursuit} & 3.5 & %.4f & %.4f & %.4f & %.4f \\\\\n", //
            avg.Get(0).number().doubleValue(), max.Get(0).number().doubleValue(), //
            avg.Get(1).number().doubleValue(), max.Get(1).number().doubleValue());
      else
        latex += String.format(Locale.US, "&& %d & %.4f & %.4f & %.4f & %.4f \\\\\n", i == 2 ? 5 : 7, //
            avg.Get(0).number().doubleValue(), max.Get(0).number().doubleValue(), //
            avg.Get(1).number().doubleValue(), max.Get(1).number().doubleValue());
      i++;
    }
    return latex + "\\hline";
  }

  public static void main(String[] args) {
    FollowingSimulator simulator = new FollowingSimulator();
    simulator.first();
    simulator.trajectoryDesign.timerFrame.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
  }
}
