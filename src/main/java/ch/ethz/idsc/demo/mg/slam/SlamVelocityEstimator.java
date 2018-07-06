// code by mg
package ch.ethz.idsc.demo.mg.slam;

import ch.ethz.idsc.retina.util.math.SI;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.qty.Quantity;
import ch.ethz.idsc.tensor.red.Norm;

// computes a velocity estimate from a pose interface and a corresponding timestamp
// NOTE first version, only linear velocity is estimated
// TODO unit of velocity is [m], maybe switch to unitless representation
public class SlamVelocityEstimator {
  private Tensor lastPose;
  private Tensor linVel;
  private Tensor angVel;
  private double lastTimeStamp; // [s]

  public void initialize(Tensor initialPose, double initialTimeStamp) {
    lastPose = initialPose;
    linVel = Tensors.of(Quantity.of(0, SI.METER), Quantity.of(0, SI.PER_METER));
    angVel = Tensors.of(RealScalar.of(0));
    lastTimeStamp = initialTimeStamp;
  }

  /** computes velocity as position difference
   * 
   * @param currentPose in the form [x,y,a]
   * @param currentTimeStamp unit [s] */
  public void updateEstimate(Tensor currentPose, double currentTimeStamp) {
    double deltaTimeStamp = currentTimeStamp - lastTimeStamp;
    linVel = (currentPose.extract(0, 2).subtract(lastPose.extract(0, 2))).divide(RealScalar.of(deltaTimeStamp));
    angVel = (currentPose.extract(2, 3).subtract(lastPose.extract(2, 3))).divide(RealScalar.of(deltaTimeStamp));
    // update references
    lastPose = currentPose;
    lastTimeStamp = currentTimeStamp;
  }

  public Tensor getAngVel() {
    return angVel;
  }

  public Tensor getLinVel() {
    return linVel;
  }

  public double getLinVelNorm2() {
    return Norm._2.ofVector(linVel).number().doubleValue();
  }
}
