// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.util.List;

/** provides an object to save all properties of an tracking evaluation instant. TrackingEvaluator contains
 * an array of such objects. Functions are provided to compare ground truth features with the estimated features */
public class TrackingEvaluatorInstant {
  private final List<ImageBlob> groundTruthFeatures;
  private final List<ImageBlob> estimatedFeatures;
  private final float[][] distToClosestEstimate;
  private final boolean[] assignedEstimatedFeatures;
  private final float maxDistance;
  private final float truePositiveThreshold;
  private float truePositiveCount;
  private float falsePositiveCount;
  private float falseNegativeCount;
  private float recall; // important parameter to quantify performance
  private float precision; // important parameter to quantify performance

  TrackingEvaluatorInstant(PipelineConfig pipelineConfig, List<ImageBlob> groundTruthFeatures, List<ImageBlob> estimatedFeatures) {
    this.groundTruthFeatures = groundTruthFeatures;
    this.estimatedFeatures = estimatedFeatures;
    distToClosestEstimate = new float[this.groundTruthFeatures.size()][2];
    assignedEstimatedFeatures = new boolean[this.estimatedFeatures.size()];
    maxDistance = pipelineConfig.maxDistance.number().floatValue();
    truePositiveThreshold = pipelineConfig.truePositiveThreshold.number().floatValue();
    // initialize distToClosestEstimate
    for (int i = 0; i < this.groundTruthFeatures.size(); i++) {
      distToClosestEstimate[i][0] = maxDistance;
      distToClosestEstimate[i][1] = this.estimatedFeatures.size();
    }
  }

  // function that runs whole evaluation
  public void compareFeatures() {
    computeDistances();
    evaluationLogic();
    // compute recall and precision
    recall = truePositiveCount / (truePositiveCount + falseNegativeCount);
    precision = truePositiveCount / (truePositiveCount + falsePositiveCount);
  }

  private void computeDistances() {
    // iterate through all combinations
    for (int i = 0; i < groundTruthFeatures.size(); i++) {
      for (int j = 0; j < estimatedFeatures.size(); j++) {
        float currentDist = groundTruthFeatures.get(i).getDistanceTo(estimatedFeatures.get(j));
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
    for (int i = 0; i < groundTruthFeatures.size(); i++) {
      if (distToClosestEstimate[i][0] < truePositiveThreshold) {
        truePositiveCount++;
        int assignedEstimatedFeature = (int) distToClosestEstimate[i][1];
        assignedEstimatedFeatures[assignedEstimatedFeature] = true;
      } else {
        falseNegativeCount++;
      }
    }
    for (int i = 0; i < estimatedFeatures.size(); i++) {
      if (!assignedEstimatedFeatures[i]) {
        falsePositiveCount++;
      }
    }
  }

  public List<ImageBlob> getGroundTruthFeatures() {
    return groundTruthFeatures;
  }

  public List<ImageBlob> getEstimatedFeatures() {
    return estimatedFeatures;
  }

  public float getRecall() {
    return recall;
  }

  public float getPrecision() {
    return precision;
  }
}
