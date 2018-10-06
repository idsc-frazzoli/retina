//code by mh
package ch.ethz.idsc.gokart.core.mpc;

import java.io.Serializable;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;

public class GokartState implements Serializable {
  //TODO: check with jph if I should use one Tensor instead.
  //(Scalars used for clarity right now
  private static final long serialVersionUID = 1L;
  /** forward velocity in gokart frame with unit m*s^1 */
  public final Scalar Ux;
  /** sidewards velocity in gokart frame with unit m*s^1  */
  public final Scalar Uy;
  /** angular velocity with unit rad*s^-1 */
  public final Scalar dotPsi;
  /** global position in X direction with unit m*/
  public final Scalar X;
  /** global position in Y direction with unit m*/
  public final Scalar Y; 
  /** orientation with unit rad */
  public final Scalar Psi;
  /** left rear wheel speed */
  public final Scalar w2L;
  /** right rear wheel speed */
  public final Scalar w2R;
  
  public GokartState(
      Scalar Ux,
      Scalar Uy,
      Scalar dotPsi,
      Scalar X,
      Scalar Y,
      Scalar Psi,
      Scalar w2L,
      Scalar w2R) {
    this.Ux= Ux;
    this.Uy = Uy;
    this.dotPsi = dotPsi;
    this.X = X;
    this.Y = Y;
    this.Psi = Psi;
    this.w2L = w2L;
    this.w2R = w2R;
    if(!checkUnits())
      throw new IllegalArgumentException();
  }
  
  public GokartState(GokartState gokartState) {
    this.Ux= gokartState.Ux;
    this.Uy = gokartState.Uy;
    this.dotPsi = gokartState.dotPsi;
    this.X = gokartState.X;
    this.Y = gokartState.Y;
    this.Psi = gokartState.Psi;
    this.w2L = gokartState.w2L;
    this.w2R = gokartState.w2R;
  }
  
  public GokartState(
      Tensor GokartStateTensor) {
    this.Ux= GokartStateTensor.Get(0);
    this.Uy = GokartStateTensor.Get(1);
    this.dotPsi = GokartStateTensor.Get(2);
    this.X = GokartStateTensor.Get(3);
    this.Y = GokartStateTensor.Get(4);
    this.Psi = GokartStateTensor.Get(5);
    this.w2L = GokartStateTensor.Get(6);
    this.w2R = GokartStateTensor.Get(7);
    if(!checkUnits())
      throw new IllegalArgumentException();
  }
  
  public Tensor getTensor() {
    return Tensors.of(
        Ux,
        Uy,
        dotPsi,
        X,
        Y,
        Psi,
        w2L,
        w2R);
  }
  
  boolean checkUnits() {
    //TODO: implement this (how can we check units?)
    return true;
  }
}
