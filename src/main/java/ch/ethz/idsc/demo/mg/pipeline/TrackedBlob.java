package ch.ethz.idsc.demo.mg.pipeline;

// this class provides a simple object for the tracked blobs
public class TrackedBlob {
  // fields
  private final float[] pos;
  private final double[][] covariance;
  private final boolean isCone;

  TrackedBlob(DavisSingleBlob davisSingleBlob) {
    pos = davisSingleBlob.getInitPos();
    covariance = davisSingleBlob.getCovariance();
    isCone = false;
  }
}
