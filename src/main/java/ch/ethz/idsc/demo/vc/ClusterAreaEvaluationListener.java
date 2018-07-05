// code by vc
package ch.ethz.idsc.demo.vc;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ch.ethz.idsc.gokart.core.perc.ClusterCollection;
import ch.ethz.idsc.gokart.core.perc.ClusterConfig;
import ch.ethz.idsc.gokart.core.perc.LidarClustering;
import ch.ethz.idsc.gokart.core.perc.LinearPredictor;
import ch.ethz.idsc.gokart.core.perc.SimplePredictor;
import ch.ethz.idsc.gokart.core.pos.GokartPoseLocal;
import ch.ethz.idsc.gokart.core.pos.LocalizationConfig;
import ch.ethz.idsc.gokart.gui.top.ObstacleClusterTrackingRender;
import ch.ethz.idsc.owl.bot.util.RegionRenders;
import ch.ethz.idsc.owl.bot.util.UserHome;
import ch.ethz.idsc.owl.gui.RenderInterface;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.owl.math.planar.Polygons;
import ch.ethz.idsc.owl.math.region.ImageRegion;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.io.Export;

public class ClusterAreaEvaluationListener {
  private static final File directory = UserHome.Pictures("clusters");
  private static final File directory1 = UserHome.Pictures("pf");
  private static final Tensor MODEL2PIXEL = Tensors.matrix(new Number[][] { //
      { 15, 0, -320 }, //
      { 0, -15, 960 }, //
      { 0, 0, 1 }, //
  });
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
  public final LidarClustering lidarClustering;

  public ClusterAreaEvaluationListener(ClusterConfig clusterConfig) {
    ImageRegion imageRegion = LocalizationConfig.getPredefinedMap().getImageRegion();
    RenderInterface create = RegionRenders.create(imageRegion);
    lidarClustering = new LidarClustering(clusterConfig, collection, () -> pose) {
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
        if (5 < count) {
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
          // System.out.println(String.format("Scan count :%s\n" + "Average perf =%6.3f\n" + "Average perf LP =%6.3f\n" + //
          // "Average recall SP =%6.3f\n" + "Average precision SP =%6.3f\n" + //
          // "Average recall LP =%6.3f\n" + "Average precision LP =%6.3f\n" + "Noise ratio =%6.3f\n", //
          // count, //
          // perfAveragedSP, perfAveragedLP, //
          // recallAveragedSP, precisionAveragedSP, //
          // recallAveragedLP, precisionAveragedLP, noiseAveraged));
          if (count == 230) {
            try {
              directory1.mkdir();
              Export.of(new File(directory1, //
                  String.format("epsilon%fminPoints%d.csv", clusterConfig.epsilon.Get().number().doubleValue(), //
                      clusterConfig.minPoints.Get().number().intValue())), //
                  Tensors.of(Tensors.fromString(
                      "{Average perf SP},{Average perf LP},{Average recall SP},{Average recall LP},{Average precision SP},{Average precision LP},{Noise ratio}"), //
                      Tensors.vectorDouble(perfAveragedSP, perfAveragedLP, recallAveragedSP, recallAveragedLP, //
                          precisionAveragedSP, precisionAveragedLP, noiseRatio)));
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
          System.out.println(count);
        }
        count++;
        GeometricLayer geometricLayer = new GeometricLayer(MODEL2PIXEL, Array.zeros(3));
        BufferedImage bufferedImage = new BufferedImage(640, 640, BufferedImage.TYPE_INT_BGR);
        Graphics2D graphics2d = bufferedImage.createGraphics();
        graphics2d.setColor(Color.white);
        graphics2d.fillRect(0, 0, 640, 640);
        create.render(geometricLayer, graphics2d);
        octr.render(geometricLayer, graphics2d);
        try {
          directory.mkdir();
          ImageIO.write(bufferedImage, "png", new File(directory, String.format("clusters%04d.png", count)));
        } catch (IOException e) {
          e.printStackTrace();
        }
      };
    };
    octr = new ObstacleClusterTrackingRender(lidarClustering);
  }

  ObstacleClusterTrackingRender octr;

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