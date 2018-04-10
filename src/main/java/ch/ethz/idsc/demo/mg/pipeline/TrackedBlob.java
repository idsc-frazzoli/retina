// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

// this class provides a simple tracked blob object for visualization and pass to next modules
public class TrackedBlob {
  // fields
  private final float[] pos;
  private final double[][] covariance;
  private boolean isCone;
  private boolean isHidden;

  TrackedBlob(float[] pos, double[][] covariance, boolean isHidden) {
    this.pos = pos;
    this.covariance = covariance;
    this.isHidden = isHidden;
  }

  public double[][] getCovariance() {
    return covariance;
  }

  public float[] getPos() {
    return pos;
  }

  public void setIsCone(boolean isCone) {
    this.isCone = isCone;
  }

  public boolean getIsCone() {
    return isCone;
  }

  public boolean getIsHidden() {
    return isHidden;
  }
}
