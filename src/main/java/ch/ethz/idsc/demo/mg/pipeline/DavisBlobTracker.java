//code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import ch.ethz.idsc.retina.dev.davis._240c.DavisDvsEvent;

public class DavisBlobTracker {
  static int quantity; // number of objects that is tracked.
  DavisSingleBlob[] trackedBlobs;
  // below all functions for the tracking

  // update the tracker with an incoming event.
  void update(DavisDvsEvent davisDvsEvent) {
  }

  void iterateThroughBlobs(DavisDvsEvent davisDvsEvent) {
    // for all blobs, calculate distance. then, pick appropriate blob
    trackedBlobs[0].calculateDistance(davisDvsEvent);
    // update appropriate blob with the event
    trackedBlobs[0].updateSingleBlob(davisDvsEvent);
  }
}
