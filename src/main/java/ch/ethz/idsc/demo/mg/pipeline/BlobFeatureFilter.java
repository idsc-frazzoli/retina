// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.util.ArrayList;
import java.util.List;

// this class incorporates prior knowledge about the cones to decide whether a tracked blob is a cone or not
public class BlobFeatureFilter {
  // algorithm parameters
  private static final int upperBoarder = 100; // [pixel] blobs with larger pos[1] are neglected (probably wall features)
  // fields
  private List<TrackedBlob> trackedBlobs;

  BlobFeatureFilter() {
    trackedBlobs = new ArrayList<>();
  }

  // after each event, the list of active blobs is sent to this function
  public void receiveBlobList(List<TrackedBlob> blobs) {
    trackedBlobs = blobs;
    // only regard region of interest
    checkPosition();
    // must be in cone shape
    checkShape();
  }

  private void checkShape() {
    for (int i = 0; i < trackedBlobs.size(); i++) {
      if (trackedBlobs.get(i).getIsCone()) {
      }
    }
  }

  private void checkPosition() {
    for (int i = 0; i < trackedBlobs.size(); i++) {
      if (trackedBlobs.get(i).getPos()[1] < upperBoarder) {
        trackedBlobs.get(i).setIsCone(true);
      }
    }
  }

  public List<TrackedBlob> getTrackedBlobs() {
    return trackedBlobs;
  }
}
