// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.util.ArrayList;
import java.util.List;

/* transforms the list of DavisSingleBlobs (image space) to a list of PhysicalBlobs (physical space.
 * This can be achieved through a homography since we know that the z coordinate of the physical objects (=0 since they are on the
 * floor).
 */
public class DavisReverseTransform {
  // fields
  private float[][] transformMatrix; // TODO find the matrix!
  List<PhysicalBlob> physicalBlobs;

  DavisReverseTransform() {
    transformMatrix = new float[][] { { 1, 0 }, { 0, 1 } };
  }

  private List<PhysicalBlob> transformBlobs(List<TrackedBlob> blobs) {
    physicalBlobs = new ArrayList<>();
    for (int i = 0; i < blobs.size(); i++) {
      PhysicalBlob singlePhysicalBlob = transformSingleBlob(blobs.get(i));
      physicalBlobs.add(singlePhysicalBlob);
    }
    return physicalBlobs;
  }

  private PhysicalBlob transformSingleBlob(TrackedBlob trackedBlob) {
    PhysicalBlob physicalBlob = new PhysicalBlob();
    return physicalBlob;
  }
}
