package ch.ethz.idsc.demo.mg;

import ch.ethz.idsc.demo.mg.pipeline.TrackedBlob;

public enum BlobContainerIO {
  ;
  public static TrackedBlob getTracked(BlobContainer bc) {
    return new TrackedBlob(bc.pos, bc.covariance, bc.timeStamp, true); // TODO check
  }

  public static BlobContainer from(TrackedBlob tb) {
    BlobContainer bc = new BlobContainer();
    bc.pos = tb.getPos();
    bc.covariance = tb.getCovariance();
    bc.timeStamp = tb.getTimeStamp();
    return bc;
  }
}
