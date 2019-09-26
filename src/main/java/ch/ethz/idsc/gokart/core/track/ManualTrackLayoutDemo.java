// code by jph
package ch.ethz.idsc.gokart.core.track;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.EmptyRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.retina.util.time.SystemTimestamp;
import ch.ethz.idsc.sophus.app.api.PathRender;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ArrayPlot;
import ch.ethz.idsc.tensor.img.ColorDataGradient;
import ch.ethz.idsc.tensor.img.ColorDataGradients;
import ch.ethz.idsc.tensor.img.ColorFormat;
import ch.ethz.idsc.tensor.img.Hue;
import ch.ethz.idsc.tensor.io.Export;
import ch.ethz.idsc.tensor.io.HomeDirectory;
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Ramp;
import ch.ethz.idsc.tensor.sca.Round;

/** used in analysis of race on 20190701 between human driver and dynamic mpc
 * 
 * https://github.com/idsc-frazzoli/retina/files/3492127/20190812_autonomous_human_racing.pdf */
public class ManualTrackLayoutDemo extends BSplineTrackDemo {
  private static final List<Integer> DEGREES = Arrays.asList(1, 2, 3, 4, 5);
  private static final ColorDataGradient COLOR_DATA_GRADIENT_STRING = //
      ColorDataGradients.CLASSIC.deriveWithOpacity(RealScalar.of(0.5));
  // ---
  private final ColorDataGradient colorDataGradient;
  private final SpinnerLabel<Integer> spinnerDegree = new SpinnerLabel<>();
  private final JToggleButton jButtonRender = new JToggleButton("render");
  private final JButton jButtonExport = new JButton("export");
  private RenderInterface renderInterface = EmptyRender.INSTANCE;

  public ManualTrackLayoutDemo() {
    timerFrame.jToolBar.add(jButtonRender);
    // ---
    spinnerDegree.setList(DEGREES);
    spinnerDegree.setValue(4);
    spinnerDegree.addToComponentReduced(timerFrame.jToolBar, new Dimension(50, 28), "steps");
    jButtonRender.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        timerFrame.geometricComponent.getModel2Pixel();
      }
    });
    ScalarTensorFunction scalarTensorFunction = //
        value -> ColorFormat.toVector(Hue.of(value.number().doubleValue(), 0.7, 1, 0.5));
    Tensor tensor = Subdivide.of(0, 1, 255).map(scalarTensorFunction);
    for (int index = 0; index < tensor.length(); index += 8)
      tensor.set(Tensors.vector(0, 0, 0, 0), index);
    colorDataGradient = new ColorDataGradient() {
      @Override
      public Tensor apply(Scalar t) {
        return tensor.get((int) (t.number().doubleValue() * 255));
      }

      @Override
      public ColorDataGradient deriveWithOpacity(Scalar opacity) {
        throw new UnsupportedOperationException();
      }
    };
    // ---
    jButtonExport.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        Tensor points_xya = getControlPointsSe2().copy();
        points_xya.set(Ramp.FUNCTION, Tensor.ALL, 2);
        try {
          File file = HomeDirectory.file(SystemTimestamp.asString() + ".csv");
          Export.of(file, points_xya.map(Round._3));
          System.out.println("exported: " + file);
        } catch (Exception exception) {
          exception.printStackTrace();
        }
      }
    });
    timerFrame.jToolBar.add(jButtonExport);
  }

  public void setCurveR2(Tensor curve) {
    renderInterface = new PathRender(Color.BLUE).setCurve(curve, false);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    renderInterface.render(geometricLayer, graphics);
    // ---
    final Tensor points_xya = getControlPointsSe2().copy();
    points_xya.set(Ramp.FUNCTION, Tensor.ALL, 2);
    if (1 < points_xya.length() && jButtonRender.isSelected()) {
      Tensor points_xyr = points_xya.map(s -> Quantity.of(s, SI.METER));
      BSplineTrack bSplineTrack = BSplineTrack.of(points_xyr, !jToggleOpen.isSelected());
      Dimension dimension = timerFrame.geometricComponent.jComponent.getSize();
      Tensor pixel2model = Inverse.of(timerFrame.geometricComponent.getModel2Pixel());
      GeometricLayer gl = GeometricLayer.of(pixel2model);
      int step = spinnerDegree.getValue();
      Tensor raster = Tensors.reserve(dimension.height / step);
      for (int y = 0; y < dimension.height; y += step) {
        Tensor row = Tensors.reserve(dimension.width / step);
        for (int x = 0; x < dimension.width; x += step)
          row.append(bSplineTrack.getNearestPathProgress(gl.toVector(x, y)));
        // for (int x = 0; x < dimension.width; x += step)
        // row.append(Boole.of(bSplineTrack.isInTrack(gl.toVector(x, y).map(s -> Quantity.of(s, SI.METER)))));
        // row.append(bSplineTrack.getNearestPathProgress(gl.toVector(x, y)));
        raster.append(row);
      }
      ColorDataGradient colorDataGradient = jToggleOpen.isSelected() //
          ? COLOR_DATA_GRADIENT_STRING
          : this.colorDataGradient;
      Tensor tensor = ArrayPlot.of(raster, colorDataGradient);
      BufferedImage bufferedImage = ImageFormat.of(tensor);
      graphics.drawImage(bufferedImage, 0, 0, //
          bufferedImage.getWidth() * step, //
          bufferedImage.getHeight() * step, null);
    }
    super.render(geometricLayer, graphics);
  }
}
