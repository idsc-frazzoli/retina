// code by vc
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Objects;

import ch.ethz.idsc.gokart.core.perc.Clusters;
import ch.ethz.idsc.gokart.core.perc.UnknownObstaclePredicate;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockListener;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.red.Mean;

/** used in {@link PresenterLcmModule} */
class ObstacleLidarRenderClustering extends LidarRender {
  // FIXME obtain real pose from lcm
  private static final Tensor EVAL_POSE = Tensors.fromString("{46.93368[m], 48.46428[m], 1.15958657}");
  // ---
  private Tensor pi = null;
  private Tensor mean = Tensors.empty();
  private Tensor oldMean = Tensors.empty(); // TODO presently
  /** LidarRayBlockListener to be subscribed after LidarRender */
  LidarRayBlockListener lrbl = new LidarRayBlockListener() {
    @Override
    public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
      Tensor points = _points;
      UnknownObstaclePredicate spacialObstaclePredicate = new UnknownObstaclePredicate();
      spacialObstaclePredicate.setPose(EVAL_POSE);
      Tensor p = Tensor.of(points.stream() //
          .filter(spacialObstaclePredicate::isObstacle) //
          .map(point -> point.extract(0, 2))); // only x,y matter
      System.out.println("Size of p:" + p.length());
      oldMean = mean;
      if (!Tensors.isEmpty(p)) {
        pi = Clusters.elkiDBSCAN(p);
        System.out.println("#clusters: " + pi.length());
        // oldMean = mean;
        mean = Tensor.of(pi.stream().map(Mean::of));
        // System.out.println("oldMean" + oldMean);
        // System.out.println("mean"+ mean);
      }
    }
  };

  public ObstacleLidarRenderClustering(GokartPoseInterface gokartPoseInterface) {
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
    if (Objects.nonNull(pi)) {
      Tensor _pi = pi;
      int i = 0;
      int size = ColorDataLists._097.size();
      // int col = 255 / pi.length();
      for (Tensor x : _pi) {
        // System.out.println("mean of x:" + Mean.of(x));
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
      for (Tensor w : mean)
        for (Tensor z : w) {
          graphics.setColor(new Color(255, 0, 0, 255));
          Point2D point2D = geometricLayer.toPoint2D(z);
          graphics.fillRect((int) point2D.getX(), (int) point2D.getY(), 5, 5);
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
      // oldMean = mean;
    }
    geometricLayer.popMatrix();
  }
}
