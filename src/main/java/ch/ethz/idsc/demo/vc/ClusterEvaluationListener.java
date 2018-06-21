// code by jph
package ch.ethz.idsc.demo.vc;

import java.nio.FloatBuffer;

import ch.ethz.idsc.gokart.core.perc.ClusterCollection;
import ch.ethz.idsc.gokart.core.perc.ClusterConfig;
import ch.ethz.idsc.gokart.core.perc.SimplePredictor;
import ch.ethz.idsc.gokart.core.perc.UnknownObstaclePredicate;
import ch.ethz.idsc.owl.math.planar.PolygonClip;
import ch.ethz.idsc.owl.math.planar.Polygons;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockListener;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.opt.TensorUnaryOperator;

public class ClusterEvaluationListener implements LidarRayBlockListener {
  final UnknownObstaclePredicate unknownObstaclePredicate = new UnknownObstaclePredicate();
  private final ClusterCollection collection = new ClusterCollection();

  /** LidarRayBlockListener to be subscribed after LidarRender */
  @Override
  public void lidarRayBlock(LidarRayBlockEvent lidarRayBlockEvent) {
    System.out.println("we have clusters" + collection.getCollection().size());
    final FloatBuffer floatBuffer = lidarRayBlockEvent.floatBuffer;
    final int position = floatBuffer.position();
    Tensor points = Tensors.empty();
    while (floatBuffer.hasRemaining()) {
      double x = floatBuffer.get();
      double y = floatBuffer.get();
      double z = floatBuffer.get();
      // no filter based on height
      points.append(Tensors.vectorDouble(x, y, z));
    }
    floatBuffer.position(position);
    // ---
    Tensor newScan = Tensor.of(points.stream() //
        .filter(unknownObstaclePredicate::isObstacle) //
        .map(point -> point.extract(0, 2))); // only x,y matterx
    if (Tensors.nonEmpty(newScan)) {
      synchronized (collection) {
        ClusterConfig.GLOBAL.dbscanTracking(collection, newScan);
        SimplePredictor sp = new SimplePredictor(collection);
        Tensor hullsSP = sp.getHullPredictions();
        Tensor meansSP = sp.getMeanPredictions();
        double evaluatePerformanceSP = evaluatePerformance(meansSP, hullsSP);
        System.out.println(String.format("perf     =%6.3f", evaluatePerformanceSP));
        PerformanceMeasures measures = computeRecall(hullsSP, newScan);
        System.out.println(measures.toString());
      }
    } else
      System.err.println("scan is empty");
  }

  private double side = 0.1;

  // basic performance measure: compute the fraction of predicted centres of clusters that are
  // in the convexHull of the new lidar scan clusters
  public double evaluatePerformance(Tensor predictedMeans, Tensor hulls) {
    int count = 0;
    for (Tensor z : predictedMeans) {
      for (Tensor hull : hulls) {
        int i = Polygons.isInside(hull, z) ? 1 : 0;
        count += i;
      }
    }
    return count / (double) predictedMeans.length();
  }

  public PerformanceMeasures computeRecall(Tensor predictedShapes, Tensor newScan) {
    Tensor results = Tensors.empty();
    Enlarger enlargedPoints = new Enlarger(newScan, side);
    System.out.println("Area of points" + enlargedPoints.getTotalArea());
    Enlarger predictedAreas = new Enlarger(predictedShapes);
    System.out.println("Area of hulls" + predictedAreas.getTotalArea());
    for (Tensor x : predictedAreas.getAreas()) {
      TensorUnaryOperator clip = PolygonClip.of(x);
      for (Tensor y : enlargedPoints.getAreas()) {
        Tensor polygonIntersect = clip.apply(y); // PolygonIntersection.of(x, y);
        if (Tensors.nonEmpty(polygonIntersect))
          results.append(polygonIntersect);
      }
    }
    Enlarger res = new Enlarger(results);
    double area = res.getTotalArea();
    System.out.println("Area of intersection" + area);
    return new PerformanceMeasures( //
        area / enlargedPoints.getTotalArea(), //
        area / predictedAreas.getTotalArea());
  }
}