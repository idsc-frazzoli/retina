// code by mg, jph
package ch.ethz.idsc.retina.app.blob.algo;

import ch.ethz.idsc.retina.app.blob.BlobTrackObj;
import ch.ethz.idsc.retina.davis._240c.DavisDvsEvent;

/* package */ interface BlobScore {
  /** computes a score value for the blobTrackObj depending on the DavisDvsEvent
   * 
   * @param blobTrackObj
   * @param davisDvsEvent
   * @return score value */
  double evaluate(BlobTrackObj blobTrackObj, DavisDvsEvent davisDvsEvent);
}
