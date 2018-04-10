// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.util.ArrayList;
import java.util.List;

// this class incorporates prior knowledge about the cones to decide whether a tracked blob is a cone or not
public class BlobFeatureFilter {
  private List<TrackedBlob> trackedBlobs;

  BlobFeatureFilter() {
    trackedBlobs = new ArrayList<>();
  }

  // after each event, the list of active blobs is sent to this function
  public void receiveBlobList(List<TrackedBlob> blobs) {
    trackedBlobs = blobs;
    checkPosition();
  }

  // check the shape of the blob and discard objects that are not in cone shape
  private void checkShape() {
  }

  // discard objects that are not on the floor (hardcoded camera pose)
  private void checkPosition() {
    for (int i = 0; i < trackedBlobs.size(); i++) {
      if (trackedBlobs.get(i).getPos()[1] > 100) {
        trackedBlobs.get(i).setIsCone(false);
      } else {
        trackedBlobs.get(i).setIsCone(true);
      }
    }
  }

  public List<TrackedBlob> getTrackedBlobs() {
    return trackedBlobs;
  }
}
