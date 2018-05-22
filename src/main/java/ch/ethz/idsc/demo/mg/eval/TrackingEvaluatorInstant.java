// code by mg
package ch.ethz.idsc.demo.mg.eval;

import java.util.List;

import ch.ethz.idsc.demo.mg.pipeline.ImageBlob;
import ch.ethz.idsc.demo.mg.pipeline.PipelineConfig;

public class TrackingEvaluatorInstant {
  private final List<ImageBlob> groundTruthInstant;
  private final List<ImageBlob> estimatedInstant;
  private final boolean[] assignedEstimatedFeatures;
  private final float[][] distToClosestEstimate;
  private final float maxDistance;
  private final float truePositiveThreshold;
  private float truePositiveCount;
  private float falsePositiveCount;
  private float falseNegativeCount;
  private float recall; // parameter to quantify performance
  private float precision; // parameter to quantify performance

  TrackingEvaluatorInstant(PipelineConfig pipelineConfig, List<ImageBlob> groundTruth, List<ImageBlob> estimated) {
    groundTruthInstant = groundTruth;
    estimatedInstant = estimated;
    distToClosestEstimate = new float[groundTruthInstant.size()][2];
    assignedEstimatedFeatures = new boolean[estimatedInstant.size()];
    maxDistance = pipelineConfig.maxDistance.number().floatValue();
    truePositiveThreshold = pipelineConfig.truePositiveThreshold.number().floatValue();
    // initialize distToClosestEstimate
    for (int i = 0; i < groundTruthInstant.size(); i++) {
      distToClosestEstimate[i][0] = maxDistance;
      distToClosestEstimate[i][1] = estimatedInstant.size();
    }
  }

  // function that runs whole evaluation
  public void compareFeatures() {
    computeDistances();
    evaluationLogic();
    // compute recall and precision
    recall = truePositiveCount / (truePositiveCount + falseNegativeCount);
    // avoid division by zero
    float denominator = truePositiveCount + falsePositiveCount;
    if (denominator == 0) {
      precision = 0;
    } else {
      precision = truePositiveCount / denominator;
    }
    // System.out.println("TP/FN/FP: "+truePositiveCount+"/"+falseNegativeCount+"/"+falsePositiveCount);
    System.out.println("Recall is " + recall);
    System.out.println("Precision is " + precision);
  }

  private void computeDistances() {
    // iterate through all combinations
    for (int i = 0; i < groundTruthInstant.size(); i++) {
      for (int j = 0; j < estimatedInstant.size(); j++) {
        float currentDist = groundTruthInstant.get(i).getDistanceTo(estimatedInstant.get(j));
        if (currentDist < distToClosestEstimate[i][0]) {
          distToClosestEstimate[i][0] = currentDist;
          distToClosestEstimate[i][1] = j;
        }
      }
    }
  }

  // count Tp, Fn and Fp. V1.0: Only based on position of features.
  private void evaluationLogic() {
    // setup for logic:
    // if distToClosestEstimate[i][0] is smaller than threshold, we have a Tp
    // if distToClosestEstimate[i][0] is larger than threshold, we have a Fn
    // all estimatedFeatures that are not assigned Tp are therefore Fp
    for (int i = 0; i < groundTruthInstant.size(); i++) {
      if (distToClosestEstimate[i][0] < truePositiveThreshold) {
        truePositiveCount++;
        int assignedEstimatedFeature = (int) distToClosestEstimate[i][1];
        assignedEstimatedFeatures[assignedEstimatedFeature] = true;
      } else {
        falseNegativeCount++;
      }
    }
    for (int i = 0; i < estimatedInstant.size(); i++) {
      if (!assignedEstimatedFeatures[i]) {
        falsePositiveCount++;
      }
    }
  }

  public float getRecall() {
    return recall;
  }

  public float getPrecision() {
    return precision;
  }
}
