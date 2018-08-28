// code by mg, jph
package ch.ethz.idsc.demo.mg.blobtrack.algo;

import ch.ethz.idsc.demo.mg.blobtrack.BlobTrackObj;
import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

/* package */ interface BlobScore {
  double evaluate(BlobTrackObj blobTrackObj, DavisDvsEvent davisDvsEvent);
}
