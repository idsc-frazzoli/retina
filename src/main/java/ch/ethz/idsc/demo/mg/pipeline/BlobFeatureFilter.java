// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.util.ArrayList;
import java.util.List;

// this class incorporates prior knowledge about the cones to decide whether a tracked blob is a cone or not
public class BlobFeatureFilter {
  private final List<TrackedBlob> trackedBlobs;

  BlobFeatureFilter() {
    trackedBlobs = new ArrayList<>();
  }

  // after each event, the list of active blobs is sent to this function
  public void receiveBlobList(List<DavisSingleBlob> activeBlobs) {
  }

  // check the shape of the blob and discard objects that are not in cone shape
  private void checkShape() {
  }

  // discard objects that are not on the floor (hardcoded camera pose)
  private void checkPosition() {
  }
}
