package ch.ethz.idsc.demo.vc;

import java.awt.geom.Area;
import java.nio.FloatBuffer;

import ch.ethz.idsc.gokart.core.perc.ClusterCollection;
import ch.ethz.idsc.gokart.core.perc.ClusterConfig;
import ch.ethz.idsc.gokart.core.perc.ClusterDeque;
import ch.ethz.idsc.gokart.core.perc.LinearPredictor;
import ch.ethz.idsc.gokart.core.perc.SimplePredictor;
import ch.ethz.idsc.gokart.core.perc.UnknownObstaclePredicate;
import ch.ethz.idsc.owl.math.planar.Polygons;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockEvent;
import ch.ethz.idsc.retina.dev.lidar.LidarRayBlockListener;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class ClusterAreaEvaluationListener implements LidarRayBlockListener {
  final UnknownObstaclePredicate unknownObstaclePredicate = new UnknownObstaclePredicate();
  private final ClusterCollection collection = new ClusterCollection();
  private double step = 0.01; // length of the step from a scan to the other,
  // 0 if we assume the clusters are not moving, TODO

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
        .map(point -> point.extract(0, 2))); // only x,y matter
    if (Tensors.nonEmpty(newScan)) {
      synchronized (collection) {
        LinearPredictor lp = new LinearPredictor(collection, step);
        Tensor hullLP = lp.getHullPredictions();
        Tensor meanLP = lp.getMeanPredictions();
        ClusterConfig.GLOBAL.dbscanTracking(collection, newScan);
        Tensor predictedHulls = Tensors.empty();
        Tensor predictedMeans = Tensors.empty();
        for (ClusterDeque x : collection.getCollection()) {
          if (Tensors.nonEmpty(x.getNonEmptyMeans())) {
            Tensor predictedMean = SimplePredictor.getMeanPrediction(x);
            Tensor predictedHull = SimplePredictor.getHullPrediction(x);
            predictedMeans.append(predictedMean);
            predictedHulls.append(predictedHull);
          }
        }
        //
        if (0 < predictedMeans.length()) {
          double evaluatePerformance = evaluatePerformance(predictedMeans, predictedHulls);
          System.out.println(String.format("perf     =%6.3f", evaluatePerformance));
        }
        double evaluatePerformanceLP = evaluatePerformance(meanLP, predictedHulls);
        System.out.println(String.format("perf LP  =%6.3f\n", evaluatePerformanceLP));
        PerformanceMeasures measures = computeRecall(predictedHulls, newScan);
        System.out.println(measures.toString());
        PerformanceMeasures measuresLP = computeRecall(hullLP, newScan);
        System.out.println("LP\n" + measuresLP.toString());
      }
    } else
      System.err.println("scan is empty");
  }

  private double side = 0.04;

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
    EnlargedPoints enlargedPoints = new EnlargedPoints(newScan, side);
    EnlargedPoints predictedAreas = new EnlargedPoints(predictedShapes);
    Area ep = predictedAreas.getArea();
    ep.intersect(enlargedPoints.getArea());
    double areaIntersection = AreaMeasure.of(ep);
    // System.out.println(String.format("Area of hulls =%6.3f\n" + //
    // "Area of hulls approx =%6.3f\nArea of points =%6.3f\n", //
    // predictedAreas.getTotalArea(), //
    // AreaMeasure.of(predictedAreas.getArea()), //
    // enlargedPoints.getTotalArea()));
    return new PerformanceMeasures( //
        areaIntersection / enlargedPoints.getTotalArea(), //
        areaIntersection / predictedAreas.getTotalArea());
  }
}