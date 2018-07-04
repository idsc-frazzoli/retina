// code by vc
package ch.ethz.idsc.demo.vc;

import java.awt.geom.Area;

import ch.ethz.idsc.gokart.core.perc.ClusterCollection;
import ch.ethz.idsc.gokart.core.perc.LidarClustering;
import ch.ethz.idsc.gokart.core.perc.LinearPredictor;
import ch.ethz.idsc.gokart.core.perc.SimplePredictor;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLocal;
import ch.ethz.idsc.owl.math.planar.Polygons;
import ch.ethz.idsc.tensor.Tensor;

public class ClusterAreaEvaluationListener {
  private final ClusterCollection collection = new ClusterCollection();
  private int count = 0;
  private double recallAveragedLP = 0;
  private double precisionAveragedLP = 0;
  private double precisionAveragedSP = 0;
  private double recallAveragedSP = 0;
  private double perfAveragedLP = 0;
  private double perfAveragedSP = 0;
  private double noiseAveraged = 0;
  private Tensor pose = GokartPoseLocal.INSTANCE.getPose();
  LidarClustering lc = new LidarClustering(collection, () -> pose) {
    private LinearPredictor linearPredictor;

    public void anteScan() {
      linearPredictor = new LinearPredictor(collection);
    };

    public void postScan(Tensor newScan, double noiseRatio) {
      SimplePredictor simplePredictor = new SimplePredictor(collection);
      Tensor hullsSP = simplePredictor.getHullPredictions();
      Tensor meansSP = simplePredictor.getMeanPredictions();
      Tensor hullsLP = linearPredictor.getHullPredictions();
      Tensor meansLP = linearPredictor.getMeanPredictions();
      double evaluatePerformanceSP = evaluatePerformance(meansSP, hullsSP);
      double evaluatePerformanceLP = evaluatePerformance(meansLP, hullsSP);
      PerformanceMeasures measuresSP = recallPrecision(hullsSP, newScan);
      PerformanceMeasures measuresLP = recallPrecision(hullsLP, newScan);
      // update average values for performance, recall and precision
      if (count > 5) { // ignore the first scans
        noiseAveraged = averageValue(noiseAveraged, noiseRatio);
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
        "Average recall LP    =%6.3f\n" + "Average precision LP =%6.3f\n" + "Noise ratio          =%6.3f\n", //
            count, //
            perfAveragedSP, perfAveragedLP, //
            recallAveragedSP, precisionAveragedSP, //
            recallAveragedLP, precisionAveragedLP, noiseAveraged));
      }
      count++;
    };
  };

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
    return count / (double) predictedMeans.length(); // TODO explore options to treat case length == 0
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

  public void setPose(Tensor pose) {
    this.pose = pose;
  }
}