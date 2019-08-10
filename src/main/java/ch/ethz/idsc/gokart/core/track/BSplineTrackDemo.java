// code by jph
package ch.ethz.idsc.gokart.core.track;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.retina.util.math.Magnitude;
import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.sophus.app.api.AbstractDemo;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
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
import ch.ethz.idsc.tensor.io.ImageFormat;
import ch.ethz.idsc.tensor.lie.CirclePoints;
import ch.ethz.idsc.tensor.mat.Inverse;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.qty.Boole;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.sca.Ramp;

/* package */ class BSplineTrackDemo extends ControlPointsDemo {
  private static final Tensor CIRCLE = CirclePoints.of(27);
  private static final ColorDataGradient COLOR_DATA_GRADIENT_STRING = //
      ColorDataGradients.CLASSIC.deriveWithOpacity(RealScalar.of(0.5));
  // ---
  private final JToggleButton jToggleView = new JToggleButton("view");
  /* package */ final JToggleButton jToggleClosed = new JToggleButton("closed");
  private final JToggleButton jButton = new JToggleButton("render");
  private final ColorDataGradient colorDataGradient;

  public BSplineTrackDemo() {
    super(true, GeodesicDisplays.R2_ONLY);
    timerFrame.jToolBar.add(jToggleView);
    timerFrame.jToolBar.add(jToggleClosed);
    timerFrame.jToolBar.add(jButton);
    jButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        timerFrame.geometricComponent.getModel2Pixel();
      }
    });
    timerFrame.geometricComponent.addRenderInterfaceBackground(AxesRender.INSTANCE);
    jToggleView.addActionListener(e -> setPositioningEnabled(!jToggleView.isSelected()));
    ScalarTensorFunction scalarTensorFunction = //
        value -> ColorFormat.toVector(Hue.of(value.number().doubleValue(), 0.7, 1, 0.5));
    Tensor tensor = Subdivide.of(0, 1, 255).map(scalarTensorFunction);
    for (int index = 0; index < tensor.length(); index += 16)
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
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    final Tensor points_xya = getControlPointsSe2().copy();
    points_xya.set(Ramp.FUNCTION, Tensor.ALL, 2);
    if (1 < points_xya.length() && jButton.isSelected()) {
      Tensor points_xyr = points_xya.map(s -> Quantity.of(s, SI.METER));
      BSplineTrack bSplineTrack = BSplineTrack.of(points_xyr, jToggleClosed.isSelected());
      Dimension dimension = timerFrame.geometricComponent.jComponent.getSize();
      Tensor pixel2model = Inverse.of(timerFrame.geometricComponent.getModel2Pixel());
      GeometricLayer gl = GeometricLayer.of(pixel2model);
      int step = 4;
      Tensor raster = Tensors.reserve(dimension.height / step);
      for (int y = 0; y < dimension.height; y += step) {
        Tensor row = Tensors.reserve(dimension.width / step);
        // for (int x = 0; x < dimension.width; x += step)
        // row.append(bSplineTrack.getNearestPathProgress(gl.toVector(x, y)));
        for (int x = 0; x < dimension.width; x += step)
          row.append(Boole.of(bSplineTrack.isInTrack(gl.toVector(x, y).map(s -> Quantity.of(s, SI.METER)))));
        // row.append(bSplineTrack.getNearestPathProgress(gl.toVector(x, y)));
        raster.append(row);
      }
      ColorDataGradient colorDataGradient = jToggleClosed.isSelected() //
          ? this.colorDataGradient
          : COLOR_DATA_GRADIENT_STRING;
      Tensor tensor = ArrayPlot.of(raster, colorDataGradient);
      BufferedImage bufferedImage = ImageFormat.of(tensor);
      graphics.drawImage(bufferedImage, 0, 0, //
          bufferedImage.getWidth() * step, //
          bufferedImage.getHeight() * step, null);
    }
    {
      for (Tensor point : points_xya) {
        geometricLayer.pushMatrix(Se2Matrix.translation(point));
        Path2D path2d = geometricLayer.toPath2D(CIRCLE.multiply(point.Get(2)));
        path2d.closePath();
        graphics.setStroke(new BasicStroke(4f));
        graphics.setColor(color(point));
        graphics.draw(path2d);
        geometricLayer.popMatrix();
      }
    }
    renderControlPoints(geometricLayer, graphics);
    if (1 < points_xya.length()) {
      Tensor points_xyr = points_xya.map(s -> Quantity.of(s, SI.METER));
      BSplineTrack bSplineTrack = jToggleClosed.isSelected() //
          ? new BSplineTrackCyclic(points_xyr)
          : new BSplineTrackString(points_xyr);
      RenderInterface renderInterface2 = new TrackRender().setTrack(bSplineTrack);
      renderInterface2.render(geometricLayer, graphics);
      if (jToggleView.isSelected()) {
        Tensor position = geometricLayer.getMouseSe2State().extract(0, 2).map(s -> Quantity.of(s, SI.METER));
        Tensor nearestPosition = bSplineTrack.getNearestPosition(position);
        geometricLayer.pushMatrix(Se2Matrix.translation(nearestPosition.map(Magnitude.METER)));
        boolean inTrack = bSplineTrack.isInTrack(position);
        graphics.setColor(inTrack ? Color.GREEN : Color.RED);
        graphics.setStroke(new BasicStroke(2f));
        graphics.draw(geometricLayer.toPath2D(CirclePoints.of(10).multiply(RealScalar.of(.3)), true));
        geometricLayer.popMatrix();
      }
    }
  }

  public Color color(Tensor point) {
    return new Color(255, 128, 128, 128);
  }

  public static void main(String[] args) {
    AbstractDemo abstractDemo = new BSplineTrackDemo();
    abstractDemo.timerFrame.jFrame.setBounds(100, 100, 600, 600);
    abstractDemo.timerFrame.jFrame.setVisible(true);
  }
}
