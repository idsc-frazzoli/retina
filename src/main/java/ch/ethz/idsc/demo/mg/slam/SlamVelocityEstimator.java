// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

// computes a velocity estimate from a pose interface and a corresponding timestamp
// NOTE first version, only linear velocity is estimated
// NOTE this will be used to propagate the state estimate
public class SlamVelocityEstimator {
  private Tensor lastPose;
  private double linVelX;
  private double linVelY;
  private double lastTimeStamp; // [s]

  public void initialize(Tensor initialPose, double initialTimeStamp) {
    lastPose = initialPose;
    lastTimeStamp = initialTimeStamp;
  }

  /** computes velocity as position difference
   * 
   * @param currentPose in the form [x,y,a]
   * @param currentTimeStamp unit [s] */
  public void updateEstimate(Tensor currentPose, double currentTimeStamp) {
    double deltaTimeStamp = currentTimeStamp - lastTimeStamp;
    linVelX = currentPose.Get(0).subtract(lastPose.Get(0)).number().doubleValue() / deltaTimeStamp;
    linVelY = currentPose.Get(1).subtract(lastPose.Get(1)).number().doubleValue() / deltaTimeStamp;
    // update references
    lastPose = currentPose;
    lastTimeStamp = currentTimeStamp;
  }

  public Tensor getVelocity() {
    return Tensors.vector(linVelX, linVelY);
  }
}
