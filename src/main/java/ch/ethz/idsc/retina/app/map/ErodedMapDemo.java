// code by jph
package ch.ethz.idsc.retina.app.map;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import javax.swing.WindowConstants;

import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.gui.win.TimerFrame;
import ch.ethz.idsc.owl.math.region.BufferedImageRegion;
import ch.ethz.idsc.sophus.app.util.LazyMouse;
import ch.ethz.idsc.sophus.app.util.LazyMouseListener;
import ch.ethz.idsc.sophus.app.util.SpinnerLabel;
import ch.ethz.idsc.sophus.lie.se2.Se2Matrix;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.mat.DiagonalMatrix;

/* package */ class ErodedMapDemo {
  private final TimerFrame timerFrame1 = new TimerFrame();
  private final TimerFrame timerFrame2 = new TimerFrame();

  public ErodedMapDemo() {
    BufferedImage bufferedImage = new BufferedImage(64, 48, BufferedImage.TYPE_BYTE_GRAY);
    {
      Graphics2D graphics = bufferedImage.createGraphics();
      graphics.setColor(Color.WHITE);
      graphics.drawRect(0, 0, 5, 3);
    }
    Tensor //
    matrix = Se2Matrix.of(Tensors.vector(1, 0, 0.3)) //
        .dot(DiagonalMatrix.of(0.1, 0.1, 1)) //
        .dot(Se2Matrix.flipY(bufferedImage.getHeight()));
    EroMap eroMap = new EroMap(bufferedImage, matrix);
    timerFrame1.geometricComponent.addRenderInterface(eroMap);
    {
      LazyMouseListener lazyMouseListener = new LazyMouseListener() {
        @Override
        public void lazyClicked(MouseEvent mouseEvent) {
          Tensor tensor = timerFrame1.geometricComponent.getMouseSe2State();
          System.out.println(tensor);
          eroMap.setPixel(tensor, mouseEvent.getButton() <= 1);
        }

        @Override
        public void lazyDragged(MouseEvent mouseEvent) {
          System.out.println("drag " + mouseEvent.getButton());
          lazyClicked(mouseEvent);
        }
      };
      new LazyMouse(lazyMouseListener).addListenersTo(timerFrame1.geometricComponent.jComponent);
    }
    timerFrame1.geometricComponent.addRenderInterfaceBackground(AxesRender.INSTANCE);
    timerFrame1.configCoordinateOffset(50, 500);
    timerFrame1.jFrame.setBounds(100, 100, 600, 600);
    timerFrame1.jFrame.setVisible(true);
    timerFrame1.jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    // ---
    SpinnerLabel<Integer> spinnerRefine = new SpinnerLabel<>();
    {
      spinnerRefine.setList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
      spinnerRefine.setValue(3);
      spinnerRefine.addToComponentReduced(timerFrame2.jToolBar, new Dimension(50, 28), "refinement");
    }
    timerFrame2.geometricComponent.addRenderInterface(new RenderInterface() {
      @Override
      public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
        int radius = spinnerRefine.getValue();
        BufferedImageRegion bufferedImageRegion = eroMap.erodedRegion(radius);
        // ---
        bufferedImageRegion.render(geometricLayer, graphics);
        // ---
        for (Tensor _x : Subdivide.of(0, 7, 23)) {
          for (Tensor _y : Subdivide.of(0, 6, 23)) {
            Tensor vector = Tensors.of(_x, _y);
            boolean isMember = bufferedImageRegion.isMember(vector);
            graphics.setColor(isMember ? Color.RED : Color.GREEN);
            Point2D point2d = geometricLayer.toPoint2D(vector);
            graphics.fill(new Rectangle2D.Double(point2d.getX() - 1, point2d.getY() - 1, 3, 3));
          }
        }
      }
    });
    timerFrame2.geometricComponent.addRenderInterfaceBackground(AxesRender.INSTANCE);
    timerFrame2.configCoordinateOffset(50, 500);
    timerFrame2.jFrame.setBounds(800, 100, 600, 600);
    timerFrame2.jFrame.setVisible(true);
    timerFrame2.jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  }

  public static void main(String[] args) {
    new ErodedMapDemo();
  }
}
