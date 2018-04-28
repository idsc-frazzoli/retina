// code by vc
package ch.ethz.idsc.gokart.gui.top;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.util.Objects;

import javax.swing.JToggleButton;

import ch.ethz.idsc.gokart.core.perc.ClusterConfig;
import ch.ethz.idsc.gokart.core.perc.UnknownObstaclePredicate;
import ch.ethz.idsc.gokart.core.pos.GokartPoseInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.map.Se2Utils;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockListener;
import ch.ethz.idsc.retina.util.gui.Colors;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.opt.ConvexHull;
import ch.ethz.idsc.tensor.red.Mean;

/** used in {@link PresenterLcmModule} */
class ObstacleClusterRender extends LidarRender implements ActionListener {
  final JToggleButton jToggleButton = new JToggleButton("cluster");
  // ---
  private boolean isClustering = false;
  private Tensor pi = null;
  private Tensor hulls = Tensors.empty();
  private Tensor mean = Tensors.empty();
  private Tensor oldMean = Tensors.empty(); // TODO presently
  /** LidarRayBlockListener to be subscribed after LidarRender */
  LidarRayBlockListener lidarRayBlockListener = new LidarRayBlockListener() {
    @Override
    public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
      if (!isClustering)
        return;
      // ---
      Tensor points = _points;
      UnknownObstaclePredicate unknownObstaclePredicate = new UnknownObstaclePredicate();
      Tensor state = gokartPoseInterface.getPose(); // units {x[m], y[m], angle[]}
      unknownObstaclePredicate.setPose(state);
      Tensor p = Tensor.of(points.stream() //
          .filter(unknownObstaclePredicate::isObstacle) //
          .map(point -> point.extract(0, 2))); // only x,y matter
      oldMean = mean;
      if (!Tensors.isEmpty(p)) {
        System.out.println("Size of p:" + p.length());
        pi = ClusterConfig.GLOBAL.elkiDBSCAN(p);
        System.out.println("#clusters: " + pi.length());
        // oldMean = mean;
        mean = Tensor.of(pi.stream().map(Mean::of));
        // System.out.println(pi);
        hulls = Tensor.of(pi.stream().map(ConvexHull::of));
        // System.out.println("oldMean" + oldMean);
        // System.out.println("mean"+ mean);
      }
    }
  };

  public ObstacleClusterRender(GokartPoseInterface gokartPoseInterface) {
    super(gokartPoseInterface);
    jToggleButton.setSelected(isClustering);
    jToggleButton.addActionListener(this);
  }

  @Override // from AbstractGokartRender
  public void protected_render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (!isClustering)
      return;
    // ---
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
      ColorDataIndexed colorDataIndexed = ColorDataLists._097;
      final int size = colorDataIndexed.size();
      {
        int i = 0;
        for (Tensor hull : hulls) {
          Color color = Colors.withAlpha(colorDataIndexed.getColor(i % size), 128);
          graphics.setColor(color);
          graphics.fill(geometricLayer.toPath2D(hull));
          ++i;
        }
      }
      {
        int i = 0;
        // int col = 255 / pi.length();
        for (Tensor x : _pi) {
          graphics.setColor(colorDataIndexed.getColor(i % size));
          for (Tensor y : x) {
            Point2D point2D = geometricLayer.toPoint2D(y);
            graphics.fillRect((int) point2D.getX() - 1, (int) point2D.getY() - 1, 3, 3);
          }
          ++i;
        }
      }
      graphics.setColor(new Color(255, 0, 0, 255));
      for (Tensor w : mean) {
        Point2D point2D = geometricLayer.toPoint2D(w);
        graphics.fillRect((int) point2D.getX(), (int) point2D.getY(), 5, 5);
      }
      graphics.setColor(new Color(0, 0, 255, 255));
      for (Tensor w : oldMean) {
        Point2D point2D = geometricLayer.toPoint2D(w);
        graphics.fillRect((int) point2D.getX(), (int) point2D.getY(), 5, 5);
      }
      // System.out.println("oldMean" + oldMean);
      // System.out.println("mean"+ mean);
      // oldMean = mean;
    }
    geometricLayer.popMatrix();
  }

  @Override // from ActionListener
  public void actionPerformed(ActionEvent actionEvent) {
    isClustering = jToggleButton.isSelected();
  }
}
