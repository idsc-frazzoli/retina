// code by mg
package ch.ethz.idsc.demo.mg;

// TODO MG some functions are not called, or not part of the "real" API
// ...  can they be used in BlobTrackObj ?
enum StaticHelper {
  ;
  // testing
  static float[] mergePos(float[] posA, float actA, float[] posB, float actB) {
    float[] pos = new float[2];
    pos[0] = 1 / (actA + actB) * (actA * posA[0] + actB * posB[0]);
    pos[1] = 1 / (actA + actB) * (actA * posA[1] + actB * posB[1]);
    return pos;
  }

  static double[][] mergeCovA(double[][] covA, float actA, double[][] covB, float actB) {
    double[][] cov = new double[2][2];
    cov[0][0] = 1 / ((actA + actB) * (actA + actB)) * (actA * actA * covA[0][0] + actB * actB * covB[0][0]);
    cov[0][1] = 1 / ((actA + actB) * (actA + actB)) * (actA * actA * covA[0][1] + actB * actB * covB[0][1]);
    cov[1][0] = 1 / ((actA + actB) * (actA + actB)) * (actA * actA * covA[1][0] + actB * actB * covB[1][0]);
    cov[1][1] = 1 / ((actA + actB) * (actA + actB)) * (actA * actA * covA[1][1] + actB * actB * covB[1][1]);
    return cov;
  }

  static double[][] mergeCovB(double[][] covA, float actA, double[][] covB, float actB) {
    double[][] cov = new double[2][2];
    cov[0][0] = 1 / (actA * actA + actB * actB) * (actA * actA * covA[0][0] + actB * actB * covB[0][0]);
    cov[0][1] = 1 / (actA * actA + actB * actB) * (actA * actA * covA[0][1] + actB * actB * covB[0][1]);
    cov[1][0] = 1 / (actA * actA + actB * actB) * (actA * actA * covA[1][0] + actB * actB * covB[1][0]);
    cov[1][1] = 1 / (actA * actA + actB * actB) * (actA * actA * covA[1][1] + actB * actB * covB[1][1]);
    return cov;
  }

  static double[][] steinerCov(double[][] covariance, float[] displacement, float activity) {
    double[][] steinerCov = new double[2][2];
    steinerCov[0][0] = covariance[0][0] + activity * displacement[1] * displacement[1];
    steinerCov[1][1] = covariance[1][1] + activity * displacement[0] * displacement[0];
    steinerCov[0][1] = covariance[0][1] - activity * displacement[0] * displacement[1];
    steinerCov[1][0] = steinerCov[0][1];
    return steinerCov;
  }

  static double[][] addCov(double[][] firstCov, double[][] secondCov) {
    double[][] addCov = new double[2][2];
    addCov[0][0] = firstCov[0][0] + secondCov[0][0];
    addCov[0][1] = firstCov[0][1] + secondCov[0][1];
    addCov[1][1] = firstCov[1][1] + secondCov[1][1];
    addCov[1][0] = addCov[0][1];
    return addCov;
  }
}
