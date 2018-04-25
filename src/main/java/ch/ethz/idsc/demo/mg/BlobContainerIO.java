package ch.ethz.idsc.demo.mg;

import ch.ethz.idsc.demo.mg.pipeline.ImageBlob;

public enum BlobContainerIO {
  ;
  public static ImageBlob getTracked(BlobContainer bc) {
    return new ImageBlob(bc.pos, bc.covariance, bc.timeStamp, true); // TODO check
  }

  public static BlobContainer from(ImageBlob tb) {
    BlobContainer bc = new BlobContainer();
    bc.pos = tb.getPos();
    bc.covariance = tb.getCovariance();
    bc.timeStamp = tb.getTimeStamp();
    return bc;
  }
}
