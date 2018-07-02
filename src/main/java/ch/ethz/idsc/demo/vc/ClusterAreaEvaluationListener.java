// code by vc
package ch.ethz.idsc.demo.vc;

import java.awt.geom.Area;
import java.nio.FloatBuffer;

import ch.ethz.idsc.gokart.core.perc.ClusterCollection;
import ch.ethz.idsc.gokart.core.perc.ClusterConfig;
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
  private int count = 0;
  private double recallAveragedLP = 0;
  private double precisionAveragedLP = 0;
  private double precisionAveragedSP = 0;
  private double recallAveragedSP = 0;
  private double perfAveragedLP = 0;
  private double perfAveragedSP = 0;

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
        LinearPredictor lp = new LinearPredictor(collection);
        Tensor hullsLP = lp.getHullPredictions();
        Tensor meansLP = lp.getMeanPredictions();
        ClusterConfig.GLOBAL.dbscanTracking(collection, newScan);
        SimplePredictor sp = new SimplePredictor(collection);
        Tensor hullsSP = sp.getHullPredictions();
        Tensor meansSP = sp.getMeanPredictions();
        double evaluatePerformanceSP = evaluatePerformance(meansSP, hullsSP);
        double evaluatePerformanceLP = evaluatePerformance(meansLP, hullsSP);
        PerformanceMeasures measuresSP = recallPrecision(hullsSP, newScan);
        PerformanceMeasures measuresLP = recallPrecision(hullsLP, newScan);
        // update average values for performance, recall and precision
        if (Double.isFinite(evaluatePerformanceLP))
          perfAveragedLP = averageValue(perfAveragedLP, evaluatePerformanceLP);
        if (Double.isFinite(evaluatePerformanceSP))
          perfAveragedSP = averageValue(perfAveragedSP, evaluatePerformanceSP);
        if (Double.isFinite(measuresLP.recall))
          recallAveragedLP = averageValue(recallAveragedLP, measuresLP.recall);
        if (Double.isFinite(measuresLP.precision))
          precisionAveragedLP = averageValue(precisionAveragedLP, measuresLP.precision);
        if (Double.isFinite(measuresSP.recall))
          recallAveragedSP = averageValue(recallAveragedSP, measuresSP.recall);
        if (Double.isFinite(measuresSP.precision))
          precisionAveragedSP = averageValue(precisionAveragedSP, measuresSP.precision);
        // printouts
        System.out.println(String.format("Scan count :%s\n" + "Average perf         =%6.3f\n" + "Average perf LP      =%6.3f\n" + //
            "Average recall SP    =%6.3f\n" + "Average precision SP =%6.3f\n" + //
            "Average recall LP    =%6.3f\n" + "Average precision LP =%6.3f\n", //
            count, //
            perfAveragedSP, perfAveragedLP, //
            recallAveragedSP, precisionAveragedSP, //
            recallAveragedLP, precisionAveragedLP));
        count++;
      }
    } else
      System.err.println("scan is empty");
  }

  public double averageValue(double old, double newValue) {
    return (old * count + newValue) / (count + 1);
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

  public PerformanceMeasures recallPrecision(Tensor predictedShapes, Tensor newScan) {
    EnlargedPoints enlargedPoints = new EnlargedPoints(newScan, side);
    EnlargedPoints predictedAreas = new EnlargedPoints(predictedShapes);
    Area ep = predictedAreas.getArea();
    ep.intersect(enlargedPoints.getArea());
    double areaIntersection = AreaMeasure.of(ep);
    return new PerformanceMeasures( //
        areaIntersection / enlargedPoints.getTotalArea(), //
        areaIntersection / predictedAreas.getTotalArea());
  }
}