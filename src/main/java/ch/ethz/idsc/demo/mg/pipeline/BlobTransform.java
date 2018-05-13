// code by mg
package ch.ethz.idsc.demo.mg.pipeline;

import java.util.ArrayList;
import java.util.List;

// Transformation of ImageBlobs to PhysicalBlobs.

public class BlobTransform {
  private List<PhysicalBlob> physicalBlobs;

  BlobTransform() {
    physicalBlobs = new ArrayList<>();
  }

  public void transformSelectedBlobs(List<ImageBlob> blobs) {
    List<PhysicalBlob> physicalBlobs = new ArrayList<>();
    for (int i = 0; i < blobs.size(); i++) {
      double[] physicalPos = TransformUtil.imageToWorld(blobs.get(i).getPos()[0], blobs.get(i).getPos()[1]);
      PhysicalBlob singlePhysicalBlob = new PhysicalBlob(physicalPos);
      physicalBlobs.add(singlePhysicalBlob);
    }
    this.physicalBlobs = physicalBlobs;
  }

  public List<PhysicalBlob> getPhysicalBlobs() {
    return physicalBlobs;
  }

  // for testing
  public static void main(String[] args) {
    TransformUtil.initialize(new PipelineConfig());
    double[] physicalPos = TransformUtil.imageToWorld(170, 100);
    System.out.println(physicalPos[0] + "/" + physicalPos[1]);
  }
}
