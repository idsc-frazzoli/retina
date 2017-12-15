// code by jph
package ch.ethz.idsc.retina.gui.gokart.top;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Objects;

import javax.imageio.ImageIO;

import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.retina.dev.zhkart.pos.GokartPoseInterface;
import ch.ethz.idsc.retina.dev.zhkart.pos.LocalizationConfig;
import ch.ethz.idsc.retina.util.math.UniformResample;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

// TODO this is not the final API:
// the points should be resampled after each scan and not before each draw!
class ResampledLidarRender extends LidarRender implements ActionListener {
  private boolean flag = false;

  public ResampledLidarRender(GokartPoseInterface gokartPoseInterface) {
    super(gokartPoseInterface);
  }

  @Override // from AbstractGokartRender
  public void protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    geometricLayer.pushMatrix(Se2Utils.toSE2Matrix(supplier.get()));
    {
      Point2D point2D = geometricLayer.toPoint2D(Tensors.vector(0, 0));
      Point2D width = geometricLayer.toPoint2D(Tensors.vector(0.1, 0));
      double w = point2D.distance(width);
      graphics.setColor(new Color(0, 128, 0, 128));
      graphics.fill(new Ellipse2D.Double(point2D.getX() - w / 2, point2D.getY() - w / 2, w, w));
    }
    if (Objects.nonNull(_points)) {
      Tensor points = _points;
      Scalar threshold = LocalizationConfig.GLOBAL.threshold;
      Scalar resampleDs = LocalizationConfig.GLOBAL.resampleDs;
      UniformResample uniformResample = new UniformResample(threshold, resampleDs);
      List<Tensor> list = uniformResample.apply(points);
      // ---
      graphics.setColor(color);
      for (Tensor pnts : list)
        for (Tensor x : pnts) {
          Point2D point2D = geometricLayer.toPoint2D(x);
          graphics.fillRect((int) point2D.getX(), (int) point2D.getY(), pointSize, pointSize);
        }
      graphics.setColor(Color.BLACK);
      int total = list.stream().mapToInt(l -> l.length()).sum();
      graphics.drawString("resampled " + total, 0, 150);
      if (flag)
        try {
          flag = false;
          BufferedImage bufferedImage = new BufferedImage(640, 640, BufferedImage.TYPE_INT_ARGB);
          Graphics imageGraphics = bufferedImage.getGraphics();
          graphics.setColor(Color.WHITE);
          for (Tensor pnts : list)
            for (Tensor x : pnts) {
              Point2D point2D = geometricLayer.toPoint2D(x);
              imageGraphics.fillRect((int) point2D.getX(), (int) point2D.getY(), pointSize, pointSize);
            }
          File file = UserHome.Pictures("map_" + System.nanoTime() + ".png");
          ImageIO.write(bufferedImage, "png", file);
          System.out.println("map exported to:\n" + file);
        } catch (Exception exception) {
          exception.printStackTrace();
        }
    }
    geometricLayer.popMatrix();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    System.out.println("request to store map");
    flag = true;
  }
}
