// code by jph,vc
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Objects;

import ch.ethz.idsc.demo.vc.ElkiTest;
import ch.ethz.idsc.gokart.core.perc.UnknownObstaclePredicate;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.tensor.DoubleScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.red.Mean;

class ObstacleLidarRenderClustering extends LidarRender {
  private static final Tensor evalPose = Tensors.fromString("{46.93368[m], 48.46428[m], 1.15958657}");

  public ObstacleLidarRenderClustering(GokartPoseInterface gokartPoseInterface) {
    super(gokartPoseInterface);
  }

  Tensor oldMean = Tensors.empty();

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
      Tensor p = Tensors.empty();
      Tensor mean = Tensors.empty();
      graphics.setColor(color);
      UnknownObstaclePredicate spacialObstaclePredicate = new UnknownObstaclePredicate();
      spacialObstaclePredicate.setPose(evalPose);
      // SimpleSpacialObstaclePredicate.createVlp16();
      for (Tensor point : points) {
        if (spacialObstaclePredicate.isObstacle(point)) {
          // Point2D point2D = geometricLayer.toPoint2D(point);
          p.append(Tensors.of(DoubleScalar.of(point.Get(0).number().doubleValue()), DoubleScalar.of(point.Get(1).number().doubleValue())));
          // graphics.fillRect((int) point2D.getX(), (int) point2D.getY(), pointSize, pointSize);
        }
      }
      System.out.println("Size of p:" + p.length());
      Tensor pi = ElkiTest.testDBSCANResults(p);
      // System.out.println(pi);
      int i = 0;
      int size = ColorDataLists._097.size();
      // int col = 255 / pi.length();
      System.out.println(pi.length());
      for (Tensor x : pi) {
        // System.out.println("mean of x:" + Mean.of(x));
        mean.append(Mean.of(x));
        // System.out.println(x);
        // System.out.println(x.length());
        Color col = new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255), 128);
        col = ColorDataLists._097.getColor(i % size);
        graphics.setColor(col);
        for (Tensor y : x) {
          for (Tensor z : y) {
            // graphics.setColor(new Color(255, 0, 0, 128));
            Point2D point2D = geometricLayer.toPoint2D(z);
            graphics.fillRect((int) point2D.getX(), (int) point2D.getY(), 3, 3);
          }
        }
        i++;
      }
      for (Tensor w : mean) {
        for (Tensor z : w) {
          graphics.setColor(new Color(255, 0, 0, 255));
          Point2D point2D = geometricLayer.toPoint2D(z);
          graphics.fillRect((int) point2D.getX(), (int) point2D.getY(), 5, 5);
        }
      }
      for (Tensor w : oldMean) {
        for (Tensor z : w) {
          graphics.setColor(new Color(0, 0, 255, 255));
          Point2D point2D = geometricLayer.toPoint2D(z);
          graphics.fillRect((int) point2D.getX(), (int) point2D.getY(), 5, 5);
        }
      }
      // System.out.println("oldMean" + oldMean);
      // System.out.println("mean"+ mean);
      oldMean = mean;
    }
    geometricLayer.popMatrix();
  }
}
